package guru.springframework.spring6restmvc.controller;

import guru.springframework.spring6restmvc.service.BeerService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;

@Controller
@AllArgsConstructor
public class BeerController {
    private final BeerService beerService;
}
