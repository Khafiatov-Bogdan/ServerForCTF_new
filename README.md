>--CTF WebSite--<

Проект на данный момент представляет собой бета версию сайта для проедения соревнований CTF.

Стек:
JDK-21
Spring Boot 3.5.7
Maven
PostgreSQL
Docker-compose
Hibernate

Assembly manual:
(При запуске проекта должен быть открыт Docker Desktop)
maven clean package
docker-compose build
docker-compose up
http://localhost:3000  client ver
http://localhost:8081  server (try http://localhost:8081/debug/public/ping )

Проект разрабатывался в условиях сжатых сроков, поэтому некоторые архитектурные решения требуют оптимизации и могут быть пересмотрены новым разработчиком в соответствии с текущими требованиями.И вообще я снимаю с себя ответственность за логику проекта;)

P.S. В проекте отсутствуют комментарии и аннотации, потому что вы не в сказку попали, думайте.