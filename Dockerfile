FROM openjdk:8-jre
VOLUME /tmp
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar

ENV MASTER_MYSQL_URL=$MASTER_MYSQL_URL
ENV MASTER_MYSQL_USERNAME=$MASTER_MYSQL_USERNAME
ENV MASTER_MYSQL_PASSWORD=$MASTER_MYSQL_PASSWORD
ENV MASTER_MYSQL_DRIVER_NAME=$MASTER_MYSQL_DRIVER_NAME

ENV SLAVE_MYSQL_URL=$SLAVE_MYSQL_URL
ENV SLAVE_MYSQL_USERNAME=$SLAVE_MYSQL_USERNAME
ENV SLAVE_MYSQL_PASSWORD=$SLAVE_MYSQL_PASSWORD
ENV SLAVE_MYSQL_DRIVER_NAME=$SLAVE_MYSQL_DRIVER_NAME

ENV QUARTZ_MYSQL_URL=$QUARTZ_MYSQL_URL
ENV QUARTZ_MYSQL_USERNAME=$QUARTZ_MYSQL_USERNAME
ENV QUARTZ_MYSQL_PASSWORD=$QUARTZ_MYSQL_PASSWORD
ENV QUARTZ_MYSQL_DRIVER_NAME=$QUARTZ_MYSQL_DRIVER_NAME

ENV REDIS_SESSION_HOST=$REDIS_SESSION_HOST
ENV REDIS_SESSION_PASSWORD=$REDIS_SESSION_PASSWORD
ENV REDIS_SESSION_PORT=$REDIS_SESSION_PORT

ENV REDIS_CACHE_HOST=$REDIS_CACHE_HOST
ENV REDIS_CACHE_PASSWORD=$REDIS_CACHE_PASSWORD
ENV REDIS_CACHE_PORT=$REDIS_CACHE_PORT

ENV AWS_S3_ACCESS_KEY=$AWS_S3_ACCESS_KEY
ENV AWS_S3_SECRET_KEY=$AWS_S3_SECRET_KEY
ENV AWS_S3_BUCKET_NAME=$AWS_S3_BUCKET_NAME

ENV FCM_DATABASE_NAME=$FCM_DATABASE_NAME
ENV FCM_SERVICE_ACCOUNT=$FCM_SERVICE_ACCOUNT

ENTRYPOINT ["java","-Dspring.datasource.master.url=${MASTER_MYSQL_URL}", \
            "-Dspring.datasource.master.username=${MASTER_MYSQL_USERNAME}", \
            "-Dspring.datasource.master.password=${MASTER_MYSQL_PASSWORD}", \
            "-Dspring.datasource.master.driverName=${MASTER_MYSQL_DRIVER_NAME}", \
            "-Dspring.datasource.slave.url=${SLAVE_MYSQL_URL}", \
            "-Dspring.datasource.slave.username=${SLAVE_MYSQL_USERNAME}", \
            "-Dspring.datasource.slave.password=${SLAVE_MYSQL_PASSWORD}", \
            "-Dspring.datasource.slave.driverName=${SLAVE_MYSQL_DRIVER_NAME}", \
            "-Dspring.datasource.quartz.url=${QUARTZ_MYSQL_URL}", \
            "-Dspring.datasource.quartz.username=${QUARTZ_MYSQL_USERNAME}", \
            "-Dspring.datasource.quartz.password=${QUARTZ_MYSQL_PASSWORD}", \
            "-Dspring.datasource.quartz.driverName=${QUARTZ_MYSQL_DRIVER_NAME}", \
            "-Dredis.session.host=${REDIS_SESSION_HOST}", \
            "-Dredis.session.password=${REDIS_SESSION_PASSWORD}", \
            "-Dredis.session.port=${REDIS_SESSION_PORT}", \
            "-Dredis.cache.host=${REDIS_CACHE_HOST}", \
            "-Dredis.cache.password=${REDIS_CACHE_PASSWORD}", \
            "-Dredis.cache.port=${REDIS_CACHE_PORT}", \
            "-Daws.s3.accessKey=${AWS_S3_ACCESS_KEY}", \
            "-Daws.s3.secretKey=${AWS_S3_SECRET_KEY}", \
            "-Daws.s3.bucketName=${AWS_S3_BUCKET_NAME}", \
            "-Dfcm.database.name=${FCM_DATABASE_NAME}", \
            "-Dfcm.service.account=${FCM_SERVICE_ACCOUNT}", \
            "-jar", "/app.jar"]
