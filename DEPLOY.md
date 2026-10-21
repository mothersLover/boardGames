# Развёртывание на сервере (Docker)

Приложение целиком запускается одной командой через Docker Compose.

## Что поднимется

| Контейнер  | Назначение                                                        | Доступен снаружи            |
|------------|-------------------------------------------------------------------|-----------------------------|
| `web`      | nginx: отдаёт сайт (React) и проксирует `/api` и `/storage`       | да, порт `HTTP_PORT` (80)   |
| `backend`  | Spring Boot (API)                                                 | нет, только через `web`     |
| `postgres` | база данных                                                       | нет                         |
| `minio`    | хранилище файлов: логотипы, музыка, видео, инструкции             | консоль только с `127.0.0.1`|

Наружу открыт один порт. Браузер ходит только на `web`, поэтому настройка CORS не нужна,
а за HTTPS-прокси нет проблем со смешанным содержимым (файлы отдаются по `/storage/...`).

## Требования

- Linux-сервер (Ubuntu/Debian) с доступом по SSH
- Docker Engine и плагин Compose v2 (`docker compose version`)
- `git`
- от 2 ГБ ОЗУ для сборки фронтенда (на меньшем объёме добавьте swap)

Если Docker ещё не установлен:

```bash
curl -fsSL https://get.docker.com | sh
sudo usermod -aG docker $USER   # после этого перезайдите по SSH
```

## Быстрый старт

```bash
git clone https://github.com/mothersLover/boardGames.git
cd boardGames

cp .env.example .env
nano .env            # обязательно смените все пароли (change-me-...)

docker compose up -d --build
```

Первая сборка занимает 5–10 минут. Когда всё поднимется (`docker compose ps` — у `backend`
статус `healthy`), откройте в браузере `http://<адрес-сервера>/`.

- Сайт: `http://<адрес-сервера>/`
- Админка: `http://<адрес-сервера>/admin/login` (логин и пароль — `ADMIN_USERNAME` / `ADMIN_PASSWORD` из `.env`)

Если репозиторий приватный, для `git clone` понадобится токен GitHub или SSH-ключ на сервере.

## Что происходит при первом запуске

Бэкенд сам готовит всё необходимое:

- создаёт таблицы в базе;
- создаёт в MinIO бакет `boardgame` с публичным чтением;
- добавляет три стартовые игры (Scythe, Champions of Midgard, 7 Wonders) с логотипами
  и типами очков для Scythe и 7 Wonders.

Для Champions of Midgard готового набора типов очков нет — добавьте их в админке:
**Игры → Изменить → Типы очков**. Без типов очков в таблице результатов партии не будет строк.

## Настройки (файл `.env`)

| Переменная            | Назначение                                              | По умолчанию |
|-----------------------|---------------------------------------------------------|--------------|
| `POSTGRES_PASSWORD`   | пароль базы (обязательно)                               | —            |
| `MINIO_ROOT_PASSWORD` | пароль MinIO, минимум 8 символов (обязательно)          | —            |
| `ADMIN_PASSWORD`      | пароль входа в админку (обязательно)                    | —            |
| `ADMIN_USERNAME`      | логин админки                                           | `admin`      |
| `POSTGRES_DB`, `POSTGRES_USER`, `MINIO_ROOT_USER` | имена БД и пользователей     | `boardgames` |
| `HTTP_PORT`           | порт сайта на сервере                                   | `80`         |
| `MINIO_CONSOLE_PORT`  | порт консоли MinIO (только `127.0.0.1`)                 | `9001`       |
| `MINIO_IMAGE`         | образ MinIO (см. примечание ниже)                       | `ghcr.io/coollabsio/minio:RELEASE.2025-10-15T17-29-55Z` |

> **Про образ MinIO.** Проект MinIO перестал публиковать готовые образы сообщества: `minio/minio`
> на Docker Hub и `quay.io/minio/minio` больше не скачиваются. Поэтому по умолчанию используется
> сборка сообщества `ghcr.io/coollabsio/minio` — это MinIO `RELEASE.2025-10-15` (последний
> официальный релиз), собранный из исходников. Версия закреплена, чтобы обновления не приходили сами.
> Если хотите использовать другой образ MinIO (например, собранный вами из исходников),
> укажите его в `MINIO_IMAGE`. Образ должен быть совместим с MinIO: приложение использует его S3 API
> и политику доступа к бакету, а `docker-compose.yml` проверяет готовность командой `mc ready local`.

Пароли БД и MinIO применяются при **первом** создании томов. Если нужно сменить их позже,
сначала поменяйте пароль внутри самого сервиса (или пересоздайте тома — данные при этом будут потеряны).

## Повседневные команды

```bash
docker compose ps                     # статус контейнеров
docker compose logs -f backend        # логи бэкенда (также: web, postgres, minio)
docker compose restart backend        # перезапуск одного сервиса
docker compose down                   # остановить (данные в томах сохранятся)
```

### Обновление до новой версии

```bash
git pull
docker compose up -d --build
```

Пересоберутся только изменившиеся образы; данные (тома `pgdata`, `miniodata`) не затрагиваются.

### Консоль MinIO

Слушает только `127.0.0.1` сервера. С вашего компьютера откройте SSH-туннель:

```bash
ssh -L 9001:127.0.0.1:9001 user@<адрес-сервера>
```

и зайдите на `http://localhost:9001` (логин и пароль — `MINIO_ROOT_USER` / `MINIO_ROOT_PASSWORD`).

## Резервные копии

```bash
# база данных
docker compose exec -T postgres pg_dump -U boardgames -d boardgames -Fc > boardgames.dump

# файлы MinIO (том целиком)
docker run --rm -v boardgames_miniodata:/data -v "$PWD":/backup alpine \
  tar czf /backup/miniodata.tar.gz -C /data .
```

Имена томов — `boardgames_pgdata` и `boardgames_miniodata` (префикс — имя проекта из `docker-compose.yml`).
Если вы меняли `POSTGRES_USER` / `POSTGRES_DB` в `.env`, подставьте их в команду `pg_dump`.

## HTTPS и домен

Самый простой путь — поставить перед приложением Caddy (он сам получает сертификат Let's Encrypt).
В `.env` задайте `HTTP_PORT=8080`, закройте порт 8080 файрволом снаружи и добавьте на сервере `Caddyfile`:

```
example.com {
    reverse_proxy 127.0.0.1:8080
}
```

Подойдёт и любой другой обратный прокси (nginx + certbot, Traefik). Загрузка больших файлов
в админке проходит через него же — не забудьте разрешить тело запроса до 200 МБ
(в nginx: `client_max_body_size 200m;`).

## Перенос данных с локальной машины (необязательно)

Если нужно перенести игры, партии и загруженные файлы, которые у вас уже есть локально.
На локальной машине достаточно Docker (Docker Desktop) — утилиты Postgres и `mc` ставить не нужно.

1. **На локальной машине** сделайте дамп базы и выгрузите бакет MinIO. Логины и пароли ниже —
   значения из локального `application.yml` (`postgres`/`admin` и `minioadmin`/`minioadmin`),
   подставьте свои, если они отличаются:

   ```bash
   mkdir transfer

   docker run --rm -e PGPASSWORD=admin -v "$PWD/transfer:/out" postgres:17-alpine \
     pg_dump -h host.docker.internal -U postgres -d boardgames -Fc -f /out/boardgames.dump

   docker run --rm -v "$PWD/transfer/files:/out" --entrypoint /bin/sh \
     ghcr.io/coollabsio/minio:RELEASE.2025-10-15T17-29-55Z -c \
     "mc alias set src http://host.docker.internal:9000 minioadmin minioadmin && mc mirror src/boardgame /out"
   ```

2. Скопируйте каталог `transfer` на сервер в каталог проекта: `scp -r transfer user@<адрес-сервера>:~/boardGames/`.
3. **На сервере** запустите стек и дождитесь первого старта — при нём создаются таблицы и бакет с правильными правами:

   ```bash
   docker compose up -d --build
   docker compose stop backend
   ```

4. Восстановите базу и залейте файлы в MinIO:

   ```bash
   docker compose exec -T postgres pg_restore -U boardgames -d boardgames \
     --clean --if-exists --no-owner < transfer/boardgames.dump

   docker compose cp transfer/files minio:/tmp/files
   docker compose exec minio sh -c \
     'mc alias set local http://127.0.0.1:9000 "$MINIO_ROOT_USER" "$MINIO_ROOT_PASSWORD" && mc mirror /tmp/files local/boardgame'
   ```

5. Запустите бэкенд: `docker compose start backend`.

Если вы меняли `POSTGRES_USER` / `POSTGRES_DB` в `.env`, подставьте свои значения вместо `boardgames`.

## Если что-то не работает

- **Порт 80 занят** — задайте другой `HTTP_PORT` в `.env` и выполните `docker compose up -d`.
- **`docker compose up` ругается на пустую переменную** — не заполнен `.env` (см. «Быстрый старт»).
- **Сайт отвечает 502 сразу после запуска** — бэкенд ещё стартует (около 30–60 секунд); смотрите `docker compose logs -f backend`.
- **Сборка фронтенда падает или «зависает»** — не хватает памяти; добавьте swap или соберите на более мощной машине.
- **Не открываются логотипы и медиа** — проверьте, что `minio` в статусе `healthy`, и откройте `http://<адрес>/storage/boardgame/` — должен ответить MinIO (ошибка доступа — это нормально, 502 — нет).

## Безопасность

- Смените все `change-me-...` пароли в `.env` до первого запуска.
- Публичная часть API (создание партий, ввод результатов) намеренно не требует входа —
  приложение рассчитано на семейное использование. Если выставляете его в открытый интернет,
  ограничьте доступ на уровне обратного прокси (Basic-аутентификация, VPN или белый список адресов).
- Файл `.env` содержит секреты и не должен попадать в git (он добавлен в `.gitignore`).
