package softuni.web;

import org.modelmapper.ModelMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import softuni.model.service.OfferServiceModel;
import softuni.service.OfferService;

import java.util.List;

@Controller
@RequestMapping("/offers")
public class OfferController {

    private final OfferService offerService;
    private final ModelMapper modelMapper;

    public OfferController(OfferService offerService, ModelMapper modelMapper) {
        this.offerService = offerService;
        this.modelMapper = modelMapper;
    }

    @GetMapping("/top-offers")
    @PreAuthorize("isAuthenticated()")

    public ModelAndView topOffers(ModelAndView modelAndView) {

        List<OfferServiceModel> allOffers = offerService.findAllOffers();

        modelAndView.addObject("offers",allOffers);
        modelAndView.setViewName("top-offers");

        return modelAndView;
    }
}
