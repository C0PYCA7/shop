package com.auction.auction.controllers;

import com.auction.auction.models.Comment;
import com.auction.auction.models.Lot;
import com.auction.auction.models.User;
import com.auction.auction.repo.CommentRepo;
import com.auction.auction.repo.LotRepo;
import com.auction.auction.repo.SubscribeRepo;
import com.auction.auction.service.LotService;
import com.auction.auction.service.SubscribeService;
import com.auction.auction.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.Banner;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

@Controller
public class MainController {


    @Autowired
    private UserService userService;
    @Autowired
    private SubscribeService subscribeService;
    @Autowired
    private LotRepo lotRepo;
    @Autowired
    private LotService lotService;
    @Autowired
    private CommentRepo commentRepo;

    @Value("${upload.path}")
    private String uploadPath;

    @GetMapping("/auction/home")
    public String HomePage(Model model) {
        Iterable<Lot> lots = lotService.getActiveLots();
        model.addAttribute("lots", lots);
        return "homePage";
    }



    @GetMapping("/auction/home/add")
    public String addLot() {
        return "addLot";
    }

    @PostMapping ("/auction/home/add")
    public String addLotPost(@RequestParam String name,
                             @RequestParam String description,
                             @RequestParam int startPrice,
                             @RequestParam int redemptionPrice,
                             @RequestParam String endDate,
                             @RequestParam("mainImg") MultipartFile mainImg,
                             Model model) throws IOException {
        User curUser = userService.getCurrentUser();
        Lot lot = new Lot(name, description, startPrice, redemptionPrice, curUser.getId(),endDate);
        File uploadDir = new File(uploadPath);
        if(!uploadDir.exists()){
            uploadDir.mkdir();
        }
        String uuidFile = UUID.randomUUID().toString();
        String res = uuidFile + "." + mainImg.getOriginalFilename();
        mainImg.transferTo(new File(uploadPath + "/" + res));
        lot.setMainImg(res);
        lotRepo.save(lot);
        return "redirect:/auction/home";
    }

    @GetMapping("/auction/home/{id}")
    public String lotById(@AuthenticationPrincipal User user,@PathVariable(value = "id") long id, Model model) {
        Optional<Lot> lot = lotRepo.findById(id);
        ArrayList<Lot> res = new ArrayList<>();
        lot.ifPresent(res::add);
        model.addAttribute("lot", res);
        ArrayList<Comment> comms = commentRepo.findByLotId(id);
        model.addAttribute("comments", comms);

        return "currentLot";
    }

    @PostMapping("/auction/home/{id}/comm")
    public String addcomm(@AuthenticationPrincipal User user,
                          @PathVariable(value = "id") long id,
                          @RequestParam String comment,
                          Model model){

        Comment comment1 = new Comment(id,comment,user.getUsername());
        commentRepo.save(comment1);

        return "redirect:/auction/home/{id}";
    }
    @GetMapping("/auction/home/profile")
    public String profile(@AuthenticationPrincipal User user, Model model) {
        ArrayList<Lot> lots = lotRepo.findByActiveFalseAndOwnerId(user.getId());
        model.addAttribute("lots", lots );
        model.addAttribute("user", user );
        return "profile";
    }

    @GetMapping("/auction/home/favorite")
    public String favoriteLots(@AuthenticationPrincipal User user, Model model) {
        ArrayList<Lot> lots = lotService.getFavoriteLots(user.getId());
        model.addAttribute("lots", lots);
        return "favorite";
    }

}