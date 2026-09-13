# Sử dụng môi trường Java 17 siêu nhẹ
FROM eclipse-temurin:17-jre-alpine

# Khai báo thư mục làm việc bên trong Container
WORKDIR /app

# Copy file .jar sau khi build từ máy thật vào Container
# Lưu ý: Tên file Restaurant-0.0.1-SNAPSHOT.jar phải khớp với cấu hình trong pom.xml
COPY target/Restaurant-0.0.1-SNAPSHOT.jar app.jar

# Mở cổng 8080 để giao tiếp với bên ngoài
EXPOSE 8080

# Lệnh khởi động Spring Boot
ENTRYPOINT ["java", "-jar", "app.jar"]