package SHmarketplace;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("SHmarketplace.mapper")
public class SHmarketplaceApplication {

    public static void main(String[] args) {
        SpringApplication.run(SHmarketplaceApplication.class, args);
    }

}