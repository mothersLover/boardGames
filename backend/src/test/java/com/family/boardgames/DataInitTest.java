package com.family.boardgames;

import com.family.boardgames.model.Game;
import com.family.boardgames.model.Player;
import com.family.boardgames.model.ScoreType;
import com.family.boardgames.model.Tag;
import com.family.boardgames.repo.GameRepository;
import com.family.boardgames.repo.PlayerRepository;
import com.family.boardgames.repo.ScoreTypeRepository;
import com.family.boardgames.repo.TagRepository;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.family.boardgames.model.Game.getScytheScoreTypes;
import static com.family.boardgames.model.Game.getSevenWondersScoreTypes;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DataInitTest {
    @Autowired
    private GameRepository gameRepository;
    @Autowired
    private TagRepository tagRepository;
    @Autowired
    private PlayerRepository playerRepository;
    @Autowired
    private ScoreTypeRepository scoreTypeRepository;

    @Test
    void dataInit() {
//        Game.GameBuilder scythe = Game.builder()
//                .name("Серп (Scythe)")
//                .description("Стратегическая игра в стиле альтернативной истории 1920-х годов. Игроки управляют фракциями, стремящимися захватить контроль над землями вокруг загадочной фабрики.")
//                .genre("Стратегия, Экономическая")
//                .minPlayers(1)
//                .maxPlayers(5)
//                .ageRating(14)
//                .price(4500.0)
//                .isActive(true);
//
//        Game.GameBuilder champions = Game.builder()
//                .name("Чемпионы Мидгарда (Champions of Midgard)")
//                .description("Игра в жанре worker placement, где игроки становятся ярлами, нанимая викингов для защиты деревни от монстров и добывая славу.")
//                .genre("Worker Placement, Фэнтези")
//                .minPlayers(2)
//                .maxPlayers(4)
//                .ageRating(12)
//                .price(3200.0)
//                .isActive(true);
//
//        Game.GameBuilder wonders = Game.builder()
//                .name("7 Чудес (7 Wonders)")
//                .description("Карточная игра, в которой игроки развивают свои цивилизации, строят чудеса света, развивают науку, культуру и военную мощь.")
//                .genre("Карточная, Цивилизация")
//                .minPlayers(2)
//                .maxPlayers(7)
//                .ageRating(10)
//                .price(2800.0)
//                .isActive(true);

//        gameRepository.save(scythe.build());
//        gameRepository.save(champions.build());
//        gameRepository.save(wonders.build());
        Game scythe1 = gameRepository.findByNameContainingIgnoreCase("Scythe").get();
        Game vikings = gameRepository.findByNameContainingIgnoreCase("Champions of Midgard").get();
        Game wonders = gameRepository.findByNameContainingIgnoreCase("7 Wonders").get();

        List<ScoreType> scoreTypes = getSevenWondersScoreTypes();
        for (ScoreType scoreType : scoreTypes) {
            scoreType.setGame(wonders);
        }
        scoreTypeRepository.saveAll(scoreTypes);

        scoreTypes = getScytheScoreTypes();
        for (ScoreType scoreType : scoreTypes) {
            scoreType.setGame(scythe1);
        }
        scoreTypeRepository.saveAll(scoreTypes);


//        Player build1 = Player.builder().displayName("Роман Хорошев").username("rHoroshev").build();
//        playerRepository.save(build1);
//        Player build2 = Player.builder().displayName("Евгений Кузьмин").username("eKuzmin").build();
//        playerRepository.save(build2);
//        Player build3 = Player.builder().displayName("Дмитрий Кузьмин").username("dKuzmin").build();
//        playerRepository.save(build3);
//        Player build4 = Player.builder().displayName("Мария Кузьмина").username("mKuzmina").build();
//        playerRepository.save(build4);
//        Player build5 = Player.builder().displayName("Александр Аполлонский").username("aApollonkiy").build();
//        playerRepository.save(build5);
//        Player build6 = Player.builder().displayName("Сергей Швабауэр").username("sShvabauer").build();
//        playerRepository.save(build6);

    }

    private Set<Tag> scytheTags(Game scythe1) {
        Set<Tag> tags = new HashSet<>();
        Tag tag1 = Tag.builder()
                .name("Стратегия")
                .description("Игры, требующие планирования и тактического мышления")
                .games(new HashSet<>())
                .build();
        tags.add(tagRepository.save(tag1));

        Tag tag2 = Tag.builder()
                .name("Альтернативная история")
                .description("Игры в сеттинге альтернативного исторического развития")
                .games(new HashSet<>())
                .build();
        tags.add(tagRepository.save(tag2));

        tags.add(Tag.builder()
                .name("Экономическая")
                .description("Игры с акцентом на экономику и управление ресурсами")
                .games(new HashSet<>())
                .build());

        tags.add(Tag.builder()
                .name("Мехи")
                .description("Игры с участием мехов или роботов")
                .games(new HashSet<>())
                .build());

        tags.add(Tag.builder()
                .name("Средняя сложность")
                .description("Игры среднего уровня сложности, подходящие для опытных игроков")
                .games(new HashSet<>())
                .build());

        return tags;
    }

    private static Set<Tag> commonTags() {
        Set<Tag> tags = new HashSet<>();
        tags.add(Tag.builder()
                .name("Настольная игра")
                .description("Классические настольные игры")
                .build());

        tags.add(Tag.builder()
                .name("Для компании")
                .description("Игры для игры в компании друзей")
                .build());

        tags.add(Tag.builder()
                .name("Хобби")
                .description("Игры для хобби и коллекционирования")
                .build());

        return tags;
    }

    private static Set<Tag> vikings() {
        Set<Tag> tags = new HashSet<>();

        tags.add(Tag.builder()
                .name("Worker Placement")
                .description("Игры, где игроки размещают рабочих для выполнения действий")
                .build());

        tags.add(Tag.builder()
                .name("Фэнтези")
                .description("Игры в фэнтезийном сеттинге")
                .build());

        tags.add(Tag.builder()
                .name("Викинги")
                .description("Игры с тематикой викингов")
                .build());

        tags.add(Tag.builder()
                .name("Кубики")
                .description("Игры с использованием игральных костей")
                .build());

        tags.add(Tag.builder()
                .name("Сражения")
                .description("Игры с механиками сражений и конфликтов")
                .build());

        return tags;
    }

    private static Set<Tag> wondersTags() {
        Set<Tag> tags = new HashSet<>();
        tags.add(Tag.builder()
                .name("Карточная")
                .description("Игры, основанные на использовании карт")
                .build());

        tags.add(Tag.builder()
                .name("Цивилизация")
                .description("Игры о развитии цивилизаций")
                .build());

        tags.add(Tag.builder()
                .name("Драфт")
                .description("Игры с механикой драфта (выбора карт)")
                .build());

        tags.add(Tag.builder()
                .name("Наука")
                .description("Игры с развитием науки и технологий")
                .build());

        tags.add(Tag.builder()
                .name("Быстрая")
                .description("Игры с коротким временем партии (до 60 минут)")
                .build());

        return tags;
    }
}
