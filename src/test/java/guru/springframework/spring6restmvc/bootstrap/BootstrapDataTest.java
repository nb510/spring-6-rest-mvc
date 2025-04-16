package guru.springframework.spring6restmvc.bootstrap;

import guru.springframework.spring6restmvc.repository.BeerRepository;
import guru.springframework.spring6restmvc.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({BootstrapData.class})
class BootstrapDataTest {
    @Autowired
    BootstrapData bootstrapData;
    @Autowired
    BeerRepository beerRepository;
    @Autowired
    CustomerRepository customerRepository;

    @Test
    void testBootstrap() {
        assertThat(beerRepository.findAll().size()).isEqualTo(3);
        assertThat(customerRepository.findAll().size()).isEqualTo(3);
    }

}