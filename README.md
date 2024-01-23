# :handshake: Business calendar

### :question: Что это?
Бэкенд приложения-календаря.

### :page_with_curl: Инструкция по запуску:
1. Скачать проект
2. Собрать проект. Перед запуском сборки необходимо запустить docker.
```shell
mvn clean package
```
3. В docker-compose файле необходимо указать переменные среды. Ниже приведен пример со значениями по умолчанию и комментариями.
Пример файла лежит в корне проекта.
```
SPRING_DATASOURCE_URL=jdbc:postgresql://calendar-db:5432/tsm - адрес базы данных.
POSTGRES_USER=root - пользователь базы данных.
POSTGRES_PASSWORD=root - пароль базы данных.

ADMIN_LOGIN=admin - логин администратора приложения по умолчанию
ADMIN_PASSWORD=admin - пароль администратора приложения по умолчанию

TOKEN_SECRET=51Ph61VOzrdwNvRa9FZclUpZRU160G1O - секрет (256-bit WEP ключ) для генерации токенов 
TOKEN_DURATION=10m - срок жизни токена
```
Для генерации секрета можно воспользоваться сервисом https://randomkeygen.com/

```
POSTGRES_DB=tsm - имя базы данных
POSTGRES_USER=root - пользователь базы данных.
POSTGRES_PASSWORD=root - пароль базы данных.

```
При необходимости можно изменить порты для приложения (по умолчанию 8080) и бд (по умолчанию 6542)

4. Запустить проект:
```shell
docker compose up
```
Swagger документация доступна по адресу: http://localhost:8080/swagger-ui/index.html
