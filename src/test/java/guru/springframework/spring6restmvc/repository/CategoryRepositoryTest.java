package guru.springframework.spring6restmvc.repository;

import guru.springframework.spring6restmvc.entities.Beer;
import guru.springframework.spring6restmvc.entities.Category;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@ActiveProfiles("localmysql")
@SpringBootTest
class CategoryRepositoryTest {

    @Container
    @ServiceConnection
    static MySQLContainer<?> mySQLContainer = new MySQLContainer<>("mysql:8");

    @Autowired
    CategoryRepository categoryRepository;
    @Autowired
    BeerRepository beerRepository;

    @Transactional
    @Test
    void testSavingBeerWithCategory() {
        Category category = categoryRepository.save(Category.builder().description("category 1").build());
        Beer beer = beerRepository.findAll().getFirst();

        beer.addCategory(category);

        beer = beerRepository.saveAndFlush(beer);

        category = categoryRepository.findById(category.getId()).get();
        assertThat(beer.getCategories()).contains(category);
        assertThat(category.getBeers()).contains(beer);
    }

}