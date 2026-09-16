package com.family.boardgames.configuration;

import com.family.boardgames.model.Game;
import com.family.boardgames.model.ScoreType;
import com.family.boardgames.repo.GameRepository;
import com.family.boardgames.repo.ScoreTypeRepository;
import com.family.boardgames.service.MinioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Заполняет базу тремя стартовыми играми при первом запуске (идемпотентно —
 * при наличии игры с похожим именем повторно не создаёт), чтобы не вбивать
 * их вручную через админку. Логотипы берутся из classpath-ресурсов и
 * загружаются в MinIO.
 */
@Slf4j
@Component
@RequiredArgsConstructor
@Order(10)
public class GameDataSeeder implements ApplicationRunner {

    private static final String LOGO_FOLDER = "games/logos";

    // Цвета для типов очков, у которых colorCode ещё не заполнен (например,
    // если строки в БД появились до того, как в билдерах Game появился цвет).
    private static final Map<String, String> SCORE_TYPE_COLORS = Map.ofEntries(
            Map.entry("Военные очки", "#c0392b"),
            Map.entry("Очки денег", "#f1c40f"),
            Map.entry("Гражданские очки", "#27ae60"),
            Map.entry("Научные очки", "#2980b9"),
            Map.entry("Коммерческие очки", "#e67e22"),
            Map.entry("Очки гильдий", "#8e44ad"),
            Map.entry("Очки чудес", "#16a085"),
            Map.entry("Популярность", "#8e44ad"),
            Map.entry("Территории", "#27ae60"),
            Map.entry("Ресурсы", "#e67e22"),
            Map.entry("Строения", "#2980b9"),
            Map.entry("Монеты", "#f1c40f")
    );

    private final GameRepository gameRepository;
    private final ScoreTypeRepository scoreTypeRepository;
    private final MinioService minioService;

    @Override
    public void run(ApplicationArguments args) {
        seedGame(
                "Scythe",
                "Scythe (Серп)",
                "Стратегическая игра в стиле альтернативной истории 1920-х годов. Игроки управляют фракциями, стремящимися захватить контроль над землями вокруг загадочной фабрики.",
                "Стратегия, Экономическая",
                1, 5, 14, 4500.0,
                "seed/scythe.png"
        );

        seedGame(
                "Champions of Midgard",
                "Champions of Midgard (Чемпионы Мидгарда)",
                "Игра в жанре worker placement, где игроки становятся ярлами, нанимая викингов для защиты деревни от монстров и добывая славу.",
                "Worker Placement, Фэнтези",
                2, 4, 12, 3200.0,
                "seed/champions.png"
        );

        seedGame(
                "7 Wonders",
                "7 Wonders (7 Чудес)",
                "Карточная игра, в которой игроки развивают свои цивилизации, строят чудеса света, развивают науку, культуру и военную мощь.",
                "Карточная, Цивилизация",
                2, 7, 10, 2800.0,
                "seed/7wonders.png"
        );

        backfillScoreTypeColors();
    }

    private void backfillScoreTypeColors() {
        List<ScoreType> toFix = new ArrayList<>();
        scoreTypeRepository.findAll().forEach(st -> {
            boolean missingColor = st.getColorCode() == null || st.getColorCode().isBlank();
            String color = SCORE_TYPE_COLORS.get(st.getName());
            if (missingColor && color != null) {
                st.setColorCode(color);
                toFix.add(st);
            }
        });
        if (!toFix.isEmpty()) {
            scoreTypeRepository.saveAll(toFix);
            log.info("Проставлены цвета по умолчанию для {} типов очков без colorCode", toFix.size());
        }
    }

    private void seedGame(String lookupSubstring, String name, String description, String genre,
                           int minPlayers, int maxPlayers, int ageRating, double price,
                           String classpathImage) {
        if (gameRepository.findByNameContainingIgnoreCase(lookupSubstring).isPresent()) {
            log.debug("Игра '{}' уже существует, пропускаем сидирование", name);
            return;
        }

        Game game = Game.builder()
                .name(name)
                .description(description)
                .genre(genre)
                .minPlayers(minPlayers)
                .maxPlayers(maxPlayers)
                .ageRating(ageRating)
                .price(price)
                .isActive(true)
                .build();
        game = gameRepository.save(game);

        try {
            ClassPathResource resource = new ClassPathResource(classpathImage);
            byte[] bytes = resource.getInputStream().readAllBytes();
            String objectKey = minioService.uploadBytes(
                    bytes, resource.getFilename(), "image/png", LOGO_FOLDER);
            game.setLogoObjectKey(objectKey);
            gameRepository.save(game);
            log.info("Игра '{}' создана, логотип загружен в MinIO: {}", name, objectKey);
        } catch (IOException e) {
            log.warn("Не удалось прочитать изображение {} для игры '{}': {}",
                    classpathImage, name, e.getMessage());
        } catch (Exception e) {
            log.warn("Не удалось загрузить логотип в MinIO для игры '{}' (MinIO недоступен?): {}",
                    name, e.getMessage());
        }
    }
}
