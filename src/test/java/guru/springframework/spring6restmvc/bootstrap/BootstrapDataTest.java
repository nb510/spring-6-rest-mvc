package guru.springframework.spring6restmvc.bootstrap;

import guru.springframework.spring6restmvc.repository.BeerRepository;
import guru.springframework.spring6restmvc.repository.CustomerRepository;
import guru.springframework.spring6restmvc.service.BeerCsvServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({BeerCsvServiceImpl.class, BootstrapData.class})
class BootstrapDataTest {
    @Autowired
    BootstrapData bootstrapData;
    @Autowired
    BeerRepository beerRepository;
    @Autowired
    CustomerRepository customerRepository;

    @Test
    void testBootstrap() {
        assertThat(beerRepository.findAll().size()).isEqualTo(2413);
        assertThat(customerRepository.findAll().size()).isEqualTo(3);
    }

}