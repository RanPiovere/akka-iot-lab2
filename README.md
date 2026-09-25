# Akka IoT Jenkins Lab 2

Учебный проект выполняет четыре практических задания семинара: компиляцию и статический анализ Scala/Akka-кода, публикацию JUnit-отчётов, матричное тестирование на JDK 11/17/21 с профилями Standard и Cluster, а также сборку и отправку Docker-образа.

## Локальная проверка

Требуются JDK 17 и sbt.

```powershell
sbt clean compile
sbt scalastyle test:scalastyle
sbt test
sbt assembly
docker build -t akka-iot-service:local .
```

JUnit XML создаётся в `target/test-reports`. Итоговый fat JAR создаётся как `target/scala-2.13/akka-iot-service.jar`.

## Настройка Jenkins

1. Создайте Pipeline с именем `Akka-IoT-Ingest-Pipeline` и выберите Pipeline script from SCM.
2. Укажите Git-репозиторий этого проекта и путь `Jenkinsfile`.
3. В Global Tool Configuration добавьте JDK с точными именами `JDK11`, `JDK17`, `JDK21`.
4. Установите плагины Pipeline, Git, JUnit, Docker Pipeline и Mailer.
5. Добавьте Docker Hub credentials с идентификатором `dockerhub-credentials-id`.
6. Задайте параметр `DOCKER_IMAGE`, например `username/akka-iot-service`.
7. Для уведомлений задайте `TEAM_EMAIL`; пустое значение отключает отправку почты.

## Получение снимков для отчёта

После первого успешного запуска сохраните Stage View, Console Output, Test Result Trend, Matrix Grid и страницу Docker Hub с тегами. В отчёте оставлены подписанные места, куда их следует вставить. Реальные снимки должны относиться к вашей Jenkins-инсталляции и вашему реестру.
