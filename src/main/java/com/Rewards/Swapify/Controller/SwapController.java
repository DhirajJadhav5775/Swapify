package com.Rewards.Swapify.Controller;

//import com.Rewards.Swapify.DTO.UesrDTO;
import com.Rewards.Swapify.DTO.UserDTO;
import com.Rewards.Swapify.OTPUtil;
import com.Rewards.Swapify.Repository.*;
import com.Rewards.Swapify.model.*;
import com.Rewards.Swapify.service.EmailService;
import com.Rewards.Swapify.service.GiftService;
import com.Rewards.Swapify.service.UserService;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Controller
public class SwapController
{
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GiftRepository giftRepository;

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private GiftService giftService;

    @Autowired
    private CartRepository cartRepository;


    @Autowired
    private EmailService emailService;

    @Autowired
    private TransactionRepository transactionRepository;

    @GetMapping("/account")
    public String account()
    {
        return "account";
    }

    @PostMapping("/registersave")
    public String registerUser(@ModelAttribute User user, Model model, HttpSession session, HttpServletResponse response) throws IOException {
        user.setFirstname(user.getFirstname().trim());
        user.setLastname(user.getLastname().trim());
        user.setEmail(user.getEmail().trim());
        user.setPassword(user.getPassword().trim());
        user.setConfirmpassword(user.getConfirmpassword().trim());

        if(!user.getPassword().equals(user.getConfirmpassword()))
        {
            response.setContentType("text/html");
            response.getWriter().println("<script>alert('Passwords do not match!'); window.location.href = '/register.html';</script>");
            return null;
        }

        //it checks if email already exists
        if(userRepository.existsByEmail(user.getEmail()))
        {
            response.setContentType("text/html");
            response.getWriter().println("<script>alert('Email Already Registered!'); window.location.href = '/register.html';</script>");
            return null;
        }

        String otp = OTPUtil.generateOTP();
        session.setAttribute("user", user);
        session.setAttribute("otp", otp);
        session.setAttribute("email", user.getEmail());
        session.setAttribute("otpGeneratedTime", System.currentTimeMillis());

        emailService.sendOTPEmail(user.getEmail(), otp);

        model.addAttribute("email", user.getEmail());
//        return "otpverify";     it will not work as we are using static html
        response.setContentType("text/html");
        response.getWriter().println("<script>alert('OTP sent to your email!'); window.location.href = '/otpverify.html';</script>");
        return null;

    }

    @PostMapping("/otp-verify")
    public String verifyOTP(@RequestParam("otp") String enteredOtp, HttpSession session, HttpServletResponse response) throws IOException {
        String sessionOtp = (String) session.getAttribute("otp");
        User user = (User) session.getAttribute("user");


        Long otpGeneratedTime = (Long) session.getAttribute("otpGeneratedTime");
        Long otpExpiryDuration = (long) (3 * 60 * 1000);

        System.out.println("OTP in Session: " + sessionOtp);
        System.out.println("Email in Session: " + session.getAttribute("email"));
        System.out.println("OTP Generated Time: " + otpGeneratedTime);
        System.out.println("Current Time: " + System.currentTimeMillis());

        if (sessionOtp == null || otpGeneratedTime == null ||
                System.currentTimeMillis() - otpGeneratedTime > otpExpiryDuration) {

            response.setContentType("text/html");
            response.getWriter().println("<script>alert('OTP has expired. Please request a new one.'); window.location.href='/forgotpass.html';</script>");
            return null;
        }

        //continue from this line
        if(sessionOtp != null && enteredOtp.equals(sessionOtp))
        {
            try{
                userRepository.save(user);
                session.removeAttribute("otp");
                session.removeAttribute("user");

                response.setContentType("text/html");
                response.getWriter().println("<script>alert('Registration Successul!'); window.location.href = '/homepage.html';</script>");
            } catch (DataIntegrityViolationException | IOException e) {
                response.setContentType("text/html");
                response.getWriter().println("<script>alert('Error saving user!'); window.location.href='/register.html';</script>");
            }
        }else {
            response.setContentType("text/html");
            response.getWriter().println("<script>alert('Invalid OTP!'); window.location.href='/verify.html';</script>");
        }
        return null;
    }

    @PostMapping("/loginsave")
    public void loginUser(@RequestParam String email, @RequestParam String password,HttpServletRequest request, HttpServletResponse response) throws IOException {
        Optional<User> user = userRepository.findByEmailAndPassword(email, password);

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        if(user.isPresent())
        {
            Cookie loginCookie = new Cookie("isLoggedIn","true");
            loginCookie.setPath("/");
            //it will expire the cookie in 1 hour otherwise it becomes a session cookie, deleted when browser closed
            loginCookie.setMaxAge(60 * 60);
            //This will make your cookie HttpOnly meaning JavaScript cannot read or modify it, protecting against XSS attacks (very important security)
            //loginCookie.setHttpOnly(true);
            response.addCookie(loginCookie);

            HttpSession session = request.getSession();
            session.setAttribute("user",user.get());
            session.setAttribute("email",email);
//            session.setAttribute("loggedInUser", user); // user is your User entity

            out.println("<script>alert('Login Successful');window.location.href = '/homepage.html';</script>");
        }
        else {
            out.println("<script>alert('Login Failed');window.location.href = '/login.html';</script>");
        }
    }
    @GetMapping("/logout")
    public void logoutUser( HttpServletRequest request,HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        //System.out.println("LOGOUT CALLED");

        if(session != null)
        {
            session.invalidate();
        }
        Cookie cookie = new Cookie("isLoggedIn", "false");
        cookie.setPath("/"  );
        cookie.setMaxAge(0);
        response.addCookie(cookie);

        response.sendRedirect("/login.html?logout=true");
    }

    //            if(session.getAttribute("email") == email)

    @PostMapping("/forgotpass")
    public String forgotpass(@RequestParam("email") String email, Model model, @ModelAttribute User user, HttpServletResponse response, HttpSession session) throws IOException {

        User loggedinUser = (User) session.getAttribute("user");
        if(!userRepository.existsByEmail(email))
        {
            response.setContentType("text/html");
            response.getWriter().println("<script>alert('OTP Not Sent, EmailID Is Not Registered!'); window.location.href='/forgotpass.html';</script>");
            return null;
        }

        else if (!email.equals(loggedinUser.getEmail()))
        {
            response.setContentType("text/html");
            response.getWriter().println("<script>alert('Email does not matched! Enter Valid MailID');window.location.href='/forgotpass.html';</script>");
            return null;
        }

        else {
            String otp = OTPUtil.generateOTP();
            session.setAttribute("otp", otp);
            session.setAttribute("user", user);
            session.setAttribute("email", user.getEmail());
            session.setAttribute("pass", user.getPassword());
            session.setAttribute("conPass",user.getConfirmpassword());

            emailService.sendOTPEmail(user.getEmail(), otp);
            //27/05/2025
            session.setAttribute("otpGeneratedTime",System.currentTimeMillis());
            //session.setAttribute("otpGeneratedTime", System.currentTimeMillis());

            model.addAttribute("email", user.getEmail());
//        return "otpverify";     it will not work as we are using static html

            response.setContentType("text/html");
            response.getWriter().println("<script>alert('OTP Sent To Your Registered Email!'); window.location.href='/newpass.html';</script>");
        }
//        else {
////            response.setContentType("text/html");
////            response.getWriter().println("<script>alert('OTP Not Sent, EmailID Is Not Registered')<script>");
//        }
//        return "newpass";
//        else {
//            response.sendRedirect("newpass.html");
//        }
        return null;
    }

    @PostMapping("/LoggedOff_forgotpass")
    public String LoggedOff_forPass(@RequestParam("email") String email, @ModelAttribute User user, HttpSession session, HttpServletResponse response) throws IOException {

//        User user = session.getAttribute("user");
        if(!userRepository.existsByEmail(email))
        {
            response.setContentType("text/html");
            response.getWriter().println("<script>alert('OTP Not Sent, EmailID Is Not Registered!'); window.location.href='/forgotpass.html';</script>");
            return null;
        }
        else
        {
            String otp = OTPUtil.generateOTP();
            session.setAttribute("otp", otp);
//            session.setAttribute("user", user);
            session.setAttribute("email", user.getEmail());

            emailService.sendOTPEmail(user.getEmail(), otp);
            session.setAttribute("otpGeneratedTime",System.currentTimeMillis());
            response.setContentType("text/html");
            response.getWriter().println("<script>alert('OTP Sent To Your Registered Email!'); window.location.href='/newpass.html';</script>");
//            response.getWriter().println("<script>alert('OTP Sent To Your Registered Email!');window.location.href=/newpass.html;</script>");
        }

        return null;
    }

    @PostMapping("/changepass")
    public String ChangePassword(@RequestParam ("otp") String enteredOTP,
                                 @RequestParam ("pass") String pass,
                                 @RequestParam ("conPass") String conPass,
                                 HttpServletResponse response,
                                 HttpSession session) throws IOException {


//        String password = (String) session.getAttribute("pass");
//        String confirmpass = (String) session.getAttribute("conpass");

        //continue from this line
//        if(sessionOtp != null && enteredOTP.equals(sessionOtp) && password.equals(confirmpass))
//        {
//
//        }
//        if (sessionOtp == null || user == null) {
//            response.setContentType("text/html");
//            response.getWriter().println("<script>alert('Session expired. Please try again.'); window.location.href='/forgotpass.html';</script>");
//            return null;
//        }
//
//        if (!enteredOTP.equals(sessionOtp))
//        {
//            response.setContentType("text/html");
//            response.getWriter().println("<script>alert('Invalid OTP.'); window.location.href='/verify.html';</script>");
//            return null;
//        }
//
//        if(!pass.equals(conPass))
//        {
//            response.setContentType("text/html");
//            response.getWriter().println("<script>alert('Passwords do not match.'); window.location.href='/verify.html';</script>");
//            return null;
//        }
//
//        user.setPassword(pass);
//        userRepository.save(user);
//
//
//        session.removeAttribute("otp");
//        session.removeAttribute("user");
//
//
//        response.setContentType("text/html");
//        response.getWriter().println("<script>alert('Password changed successfully!'); window.location.href='/login.html';</script>");
//        /*
//         String sessionOtp = (String) session.getAttribute("otp");
//        User user = (User) session.getAttribute("user");
//
//        if(sessionOtp != null && enteredOtp.equals(sessionOtp))
//        {
//            try{
//                userRepository.save(user);
//                session.removeAttribute("otp");
//                session.removeAttribute("user");
//
//                response.setContentType("text/html");
//                response.getWriter().println("<script>alert('Registration Successul!'); window.location.href = '/homepage.html';</script>");
//            } catch (DataIntegrityViolationException | IOException e) {
//                response.setContentType("text/html");
//                response.getWriter().println("<script>alert('Error saving user!'); window.location.href='/register.html';</script>");
//            }
//        }else {
//            response.setContentType("text/html");
//            response.getWriter().println("<script>alert('Invalid OTP!'); window.location.href='/verify.html';</script>");
//        }
//        return null;
//        */
//
//        return null;
        String sessionOtp = (String) session.getAttribute("otp");
//        User user = (User) session.getAttribute("user");
        String email = (String) session.getAttribute("email");
        Long otpGeneratedTime = (Long) session.getAttribute("otpGeneratedTime");

        Long otpExpiryDuration = (long) (3 * 60 * 1000);

        System.out.println("OTP in Session: " + sessionOtp);
        System.out.println("Email in Session: " + email);
        System.out.println("OTP Generated Time: " + otpGeneratedTime);
        System.out.println("Current Time: " + System.currentTimeMillis());

        if (sessionOtp == null || otpGeneratedTime == null ||
                System.currentTimeMillis() - otpGeneratedTime > otpExpiryDuration) {

            response.setContentType("text/html");
            response.getWriter().println("<script>alert('OTP has expired. Please request a new one.'); window.location.href='/forgotpass.html';</script>");
            return null;
        }

        if (sessionOtp != null && enteredOTP.equals(sessionOtp)) {
            if (pass.equals(conPass)) {
                User user = userRepository.findByEmail(email);

//                && session.getAttribute("email") == user
                if(user != null )
                {
                    user.setPassword(pass);  // Optionally encode it here if using BCrypt
                    userRepository.save(user);  // This updates the existing user

                    response.setContentType("text/html");
                    response.getWriter().println("<script>alert('Password Changed Successfully!'); window.location.href='/login.html';</script>");
                    return null;
                }
                else
                {
                    response.setContentType("text/html");
                    response.getWriter().println("<script>alert('User not found in DB!'); window.location.href='/verify.html';</script>");
                    return null;
                }

            } else {
                response.setContentType("text/html");
                response.getWriter().println("<script>alert('Passwords do not match!'); window.location.href='/verify.html';</script>");
                return null;
            }
        } else {
            response.setContentType("text/html");
            response.getWriter().println("<script>alert('Invalid OTP!'); window.location.href='/verify.html';</script>");
            return null;
        }
    }


//    @GetMapping("someProtectedPage")
//    public String somePage(HttpSession session)
//    {
//        if(session.getAttribute("user") == null)
//        {
//            return  "redirect:/login.html";
//        }
//        return "protectedpage.html";
//    }

//    @GetMapping("/adminLogin")
//    public String adminLogin() throws IOException {
////        response.sendRedirect("/adminlogin.html");
//        return "adminlogin";
//    }
//    @GetMapping("/Admin/adminLogin")
//    public String adminLogin()
//    {
//        return "/adminlogin.html";
//    }

    @GetMapping("/adminLogin")
    public String loginPage() {
        return "redirect:/adminlogin.html"; // Redirect to the static HTML file
    }

    @PostMapping("/SecureAdmin/login")
    public String adminLogin(@RequestParam String username, @RequestParam String password, HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        String adminEmail = "admin7979@gmail.com";
        String adminPassword = "DhirajAdmin@7979";
        String adminName = "Dhiraj";

        if(username.equals(adminEmail) && password.equals(adminPassword))
        {
            //out.println("<script>alert('Admin Login Successful');</script>");
            request.getSession().setAttribute("adminLoggedIn",true);
            request.getSession().setAttribute("Dhiraj",adminName);
//            response.sendRedirect("/adminDashboard.html");
            Cookie loginCookie = new Cookie("isAdmin","true");
            loginCookie.setPath("/");
            //it will expire the cookie in 1 hour otherwise it becomes a session cookie, deleted when browser closed
            loginCookie.setMaxAge(60 * 60);
            //This will make your cookie HttpOnly meaning JavaScript cannot read or modify it, protecting against XSS attacks (very important security)
            //loginCookie.setHttpOnly(true);
            response.addCookie(loginCookie);

            out.println("<script>alert('Admin Login Successful');window.location.href = '/Admin/adminDashboard';</script>");
        }
        else {
//            response.sendRedirect("/adminlogin.html?error=true");
            out.println("<script>alert('Login Failed!');window.location.href = '/adminLogin.html';</script>");
        }
        return null;
    }

    @GetMapping("/Admin/logout")
    public void adminLogout(HttpServletResponse response, HttpServletRequest request) throws IOException {
        HttpSession session = request.getSession(false);

        if(session != null)
        {
            session.invalidate();
        }

        Cookie cookie = new Cookie("isAdmin", "false");
        cookie.setPath("/");
        cookie.setMaxAge(0);

        PrintWriter out = response.getWriter();
        response.addCookie(cookie);

        out.println("<script>alert('Admin Logout!');window.location.href= '/adminlogin.html';</script>");

//        response.sendRedirect("/adminlogin.html? logout = true");

        /*
        *  HttpSession session = request.getSession(false);
        //System.out.println("LOGOUT CALLED");

        if(session != null)
        {
            session.invalidate();
        }
        Cookie cookie = new Cookie("isLoggedIn", "false");
        cookie.setPath("/"  );
        cookie.setMaxAge(0);
        response.addCookie(cookie);

        response.sendRedirect("/login.html?logout=true");
        * */
    }

    @GetMapping("/Admin/adminDashboard")
    public String adminDashboard(HttpSession session, HttpServletResponse response, HttpServletRequest request, Model model) throws IOException {


        LocalDate today = LocalDate.now();

        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().atTime(LocalTime.MAX);

        int newUsers = userRepository.countUsersByCreatedDate(startOfDay, endOfDay);
        long vouchersListed = giftRepository.countGiftsByOrderDateBetween(startOfDay, endOfDay);
//        int transactions = transactionRepository.countByTransactionDate(today);
//        double revenue = transactionRepository.sumRevenueByDate(today);

//        double vouchersBought giftRepository.

        Boolean isAdmin = (Boolean) session.getAttribute("adminLoggedIn");
        Long totalUsers = userService.countusers();
        model.addAttribute("totalUsers", totalUsers);

        long totalGifts = giftService.countgifts();
        model.addAttribute("totalGifts",totalGifts);

//        long giftBuyToday = giftService.countGiftsBuyToday();

        List<Gift> todayBuyOrders = giftRepository.findByIsPurchasedTrueAndOrderDateBetween(
                today.atStartOfDay(),
                today.plusDays(1).atStartOfDay()
        );
        model.addAttribute("buyOrder", todayBuyOrders.size());

        long giftsSoldToday = giftService.countGiftsSoldToday(); // Get count of gifts sold today
        model.addAttribute("sellOrder", giftsSoldToday);

        List<Gift> recentVouchers = giftService.getRecentVouchers();
        System.out.println("Recent Vouchers Count: " + recentVouchers.size()); // Debugging line

        for(Gift i : recentVouchers)
        {
            System.out.println("gift: " + i);
        }

        model.addAttribute("recentVouchers",recentVouchers);
        model.addAttribute("newUsers", newUsers);
        model.addAttribute("vouchersListed", vouchersListed);
//        System.out.println("Recent Vouchers Count: " + recentVouchers.size()); // Debugging line


//        long sellOrderCount = giftService.countSellOrdersForToday();
//        model.addAttribute("sellOrder",sellOrderCount);

        if(isAdmin != null && isAdmin)
        {
            return "Admin/adminDashboard";
        }
        else{
            PrintWriter out = response.getWriter();
            out.println("<script>alert('Please Log In First');window.location.href='/homepage.html';</script>");
        }
        return null;
    }

    @GetMapping("/Admin/users")
    public String users(Model model)
    {
        List<User> users = userService.getAllUsers();
        System.out.println("Fetched Users: " + users); // Log the fetched users

        model.addAttribute("users",users);

        List<UserDTO> userDTOs = new ArrayList<>();

        for(User user : users)
        {
            UserDTO dto = new UserDTO();
            dto.setId(user.getId());
            dto.setFirstname(user.getFirstname());
            dto.setLastname(user.getLastname());
            dto.setEmail(user.getEmail());

            dto.setBuyVoucherCount(giftRepository.countByBuyerId(user.getId()));
            dto.setSellVoucherCount(giftRepository.countBySeller1Id(user.getId()));

            userDTOs.add(dto);
        }
        model.addAttribute("users",userDTOs);
        return "Admin/users";
    }

//    @GetMapping("/Admin/deleteUser/{id}")
//    public void deleteUser(@PathVariable int id)
//    {
//        System.out.println("inside delete user controller with id:" + id);
//        userService.deleteUser(id);
//    }

    @DeleteMapping("/Admin/deleteUser/{id}")
    @ResponseBody
    public ResponseEntity<Void> deleteUser (@PathVariable int id) {
//        userService.deleteUserById(id); // Call your service to delete the user
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/Admin/vouchers")
    public String managevouchers(Model model)
    {
        List<Gift> vouchers = giftService.findAll();
        System.out.println("gifts in repository" + vouchers);

        model.addAttribute("vouchers", vouchers);

        return "Admin/vouchers";
    }

    @GetMapping("/Admin/transactions")
    public String transactions(Model model)
    {


        List<Transactions> transactions = transactionRepository.findAllByOrderByDateTimeDesc();
        model.addAttribute("transactions",transactions);

        return "Admin/transactions";
    }

    @DeleteMapping("/Admin/vouchers/{id}")
    @ResponseBody
    public ResponseEntity<Void> deleteVoucher(@PathVariable int id)
    {
        System.out.println("Inside delete controller with id: " + id);
        giftService.deleteVoucherByID(id);
        return ResponseEntity.noContent().build();
    }

//    @DeleteMapping("/Admin/deleteVoucher")

    @GetMapping("/myorders")
    public String orders(Model model, HttpSession session)
    {
//        List<User> users = userService.getAllUsers();
//        List<UserDTO> userDTOS = new ArrayList<>();

//        String currentUserEmail = authentication.name();
//        User currentUser = userService.findByEmail(currentUserEmail);

//        System.out.println("");
//        String currentEmail = principal.getName();
        User currentUser = (User) session.getAttribute("user");

        if (currentUser == null) {
            return "redirect:/login";
        }

        Long userId = currentUser.getId();

        List<Gift> orders = new ArrayList<>();
        Set<Integer> seenIds = new HashSet<>();
//        List<Gift> all = new ArrayList<>();

        for (Gift gift : giftRepository.findByBuyerId(userId)) {
            if (gift != null && seenIds.add(gift.getId())) {
                orders.add(gift);
            }
        }
        for (Gift gift : giftRepository.findBySeller1Id(userId)) {
            if (gift != null && seenIds.add(gift.getId())) {
                orders.add(gift);
            }
        }



        int buyCount = giftService.getBuyVoucherCountByUserId(userId);
        int sellCount = giftService.getSellVoucherCountByUserId(userId);

        model.addAttribute("orders",orders);
//        orders.forEach(gift ->
//                System.out.println("[DEBUG] Order id=" + gift.getId() + " status='" + gift.getStatus() + "'"));

        model.addAttribute("buyVoucherCount", buyCount);
        model.addAttribute("sellVoucherCount", sellCount);
        System.out.println("User ID from session: " + currentUser.getId());
//        int buyCount = giftService.getBuyVoucherCountByUserId(userId);
        System.out.println("Buy count returned: " + buyCount);

        return "myorders";

//
//        return "myorders";
    }

    // Add to Cart
    @PostMapping("/addtocart/{id}")
    public String addToCart(@PathVariable int id, HttpSession session) {

        Gift gift = giftRepository.findById(id).orElse(null);
        if (gift == null) return "redirect:/buygift";
        String email = (String) session.getAttribute("email");

        boolean alreadyExists = cartRepository.existsByEmailAndGiftId(email, id);
        if (!alreadyExists) {
//            GiftCart giftCart = new GiftCart();
//            giftCart.setEmail(email);
//            giftCart.setGiftId(id);
//            cartRepository.save(giftCart);
            GiftCart giftCart = new GiftCart();
            giftCart.setEmail((String) session.getAttribute("email"));
            giftCart.setBrand(gift.getBrand());
            giftCart.setGiftId((long)gift.getId());
            giftCart.setDetails(gift.getDetails());
            giftCart.setPrice((double)gift.getPrice());
            giftCart.setExpdate(gift.getExpdate());
            giftCart.setSeller(gift.getSeller());
            giftCart.setPromocode(gift.getPromocode());

            cartRepository.save(giftCart);
        }


//
//
//        cartRepository.save(giftCart);

        return "redirect:/cart";
    }


    @GetMapping("/cart")
    public String cart(Model model, HttpSession session)
    {
        String userEmail = (String) session.getAttribute("email");
        if (userEmail == null) {
            return "redirect:/login";
        }

        List<GiftCart> giftCarts = cartRepository.findByEmail(userEmail);
        model.addAttribute("giftCarts", giftCarts);
        return "cart";
    }

    @PostMapping("/deleteCart/{id}")
    public String deleteCartItem(@PathVariable Long id, HttpSession session)
    {
        String email = (String) session.getAttribute("email");

        GiftCart item = cartRepository.findById(id).orElse(null);
        if (item != null && item.getEmail().equals(email)) {
            cartRepository.deleteById(id);
        }

        return "redirect:/cart";
    }

//    @GetMapping("/Admin/transactions")
//    public String trasactions(Model model)
//    {
//        List<Transaction> transactions = transactionRepository.findAllByOrderByDateTimeDesc();
//        model.addAttribute("transactions",transactions);
//
//        return "Admin/transactions";
//    }

    @GetMapping("/Admin/settings")
    public String settings()
    {
        return "Admin/settings";
    }

//    @GetMapping("/admin/logout")
//    public void adminLogout(HttpServletResponse response) throws IOException {
//        response.sendRedirect("/adminlogin.html");
//    }

    @GetMapping("/Admin/messages")
    public String messages(Model model)
    {
        List<Contactus> messages = contactRepository.findAll();
        model.addAttribute("messages", messages);
        return "Admin/messages";
    }

    @PostMapping("/sellGift")
    public void SellGift(@ModelAttribute Gift gift, HttpServletResponse response, HttpSession session, Model model) throws IOException {
        gift.setBrand(gift.getBrand().trim());
        gift.setDetails(gift.getDetails().trim());
        gift.setPromocode(gift.getPromocode().trim());
        gift.setExpdate(gift.getExpdate().trim());
//        gift.setSeller((String) session.getAttribute("email"));

        String sellerEmail = (String) session.getAttribute("email");
        User currentUser = (User) session.getAttribute("user");

        if(sellerEmail != null)
        {
//            gift.setSeller(sellerEmail.trim());
            gift.setSeller(currentUser.getEmail());
            gift.setSeller1(currentUser);
        }
        else
        {
            response.setContentType("text/html");
            PrintWriter out = response.getWriter();
            out.println("<script>alert('Seller email not found in session.');window.location.href = '/sellgift.html';</script>");
            return;
        }

        // Set the order date to the current date and time
        gift.setOrderDate(LocalDateTime.now()); // Set the current date and time
        PrintWriter out = response.getWriter();
        try{

//            List<Gift> showGifts = giftRepository.findByStatus("available");

            gift.setStatus("available");
            System.out.println("[DEBUG] Saving gift with seller1.id = " + currentUser.getId());
            System.out.println("Gift before saving: " + gift);

//            gift.setSeller1(cu);
            giftRepository.save(gift);
            Transactions transactions = new Transactions();
            transactions.setEmail(sellerEmail); // store email or name
            transactions.setBrand(gift.getBrand());
            transactions.setPrice(gift.getPrice());
            transactions.setAction("sold");
            transactions.setDateTime(LocalDateTime.now());

            transactionRepository.save(transactions);


//            ribute("recentGift",recentVouchers);

            response.setContentType("text/html");
            out.println("<script>alert('Voucher Sold Succesfully');window.location.href = '/sellgift.html';</script>");
//            response.setContentType("text/html");
//            response.getWriter().println("Voucher Sold Successfully");
        }
        catch (DataIntegrityViolationException exception)
        {
            response.setContentType("text/html");
//            response.getWriter().println("Unable to sell voucher");
            out.println("<script>alert('Unable to sold voucher');window.location.href = '/sellgift.html';</script>");
        }
    }

    @PostMapping("/deleteSoldVoucher/{id}")
    public String deleteSoldVoucher(@PathVariable("id") int id) {
        giftRepository.deleteById(id);
        return "redirect:/myorders";
    }


//    public String getSellOrders(@RequestParam("date")String dateString, Model model)
//    {
//        LocalDate date = LocalDate.parse(dateString);
//        List<Gift> sellOrders = giftService.getSellOrdersByDate(date);
//        model.addAttribute("sellOrders", sellOrders);
//        model.addAttribute("selectedDate", date);
//        return "Admin/sellOrders";
//    }

    @GetMapping("/buygift")       //for displaying vouchers
    public String showAvailableGifts(Model model, HttpServletResponse response, HttpSession session)
    {
        String email = (String) session.getAttribute("email");


        List<Gift> allAvailableGifts = giftRepository.findByStatus("available");

        for (Gift gift : allAvailableGifts) {
//            if (!gift.isExpired() && gift.getExpdate().isBefore(LocalDate.now())) {
            if (!gift.isExpired() && LocalDate.parse(gift.getExpdate()).isBefore(LocalDate.now())) {
                gift.setExpired(true);
                giftRepository.save(gift); // update the database
            }
        }
        Object user = session.getAttribute("user");
        model.addAttribute("user", user);

        List<Gift> availableGifts = giftRepository.findAvailableGiftsExcludingSeller(email)
                .stream().filter(gift -> !gift.isExpired())
                .collect(Collectors.toList());

        System.out.println("Gifts from DB: " + availableGifts); // Debug print

        List<Long> cartGiftIds = cartRepository.findByEmail(email)
                .stream()
                .map(GiftCart::getGiftId)
                .collect(Collectors.toList());

        for (Gift gift : availableGifts) {
            gift.setAddedToCart(cartGiftIds.contains(gift.getId()));
        }


        model.addAttribute("gifts", availableGifts);

        return "buygift";
    }

    @PostMapping("/buynow/{id}")
    public String buyGift(@PathVariable int id, HttpSession session, Model model) {
        Gift gift = giftRepository.findById(id).orElseThrow();
        model.addAttribute("gift",gift);

        try {
            RazorpayClient client = new RazorpayClient("rzp_test_ddEiRA903Pqfod", "3vCRtLvpGwKo4yYB6OuHtY2B");

            JSONObject options = new JSONObject();
            options.put("amount", gift.getPrice() * 100); // amount in paise
            options.put("currency", "INR");
            options.put("receipt", "txn_" + UUID.randomUUID());

            Order order = client.orders.create(options);
            String orderId = order.get("id");
            model.addAttribute("orderId", orderId);

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("orderId", null);
        }

        return "payment";
    }

//    @GetMapping("/payment")
//    public String payment()
//    {
//        return "redirect:/payment";
//    }

//    @PostMapping("/createOrder")
//    @ResponseBody
//    public String createOrder(@RequestParam("amount") int amount) {
//        try {
//            RazorpayClient client = new RazorpayClient("rzp_test_ddEiRA903Pqfod", "3vCRtLvpGwKo4yYB6OuHtY2B");
//
//            JSONObject options = new JSONObject();
//            options.put("amount", amount * 100); // Amount in paise
//            options.put("currency", "INR");
//            options.put("receipt", "txn_123456");
//
//            Order order = client.orders.create(options);
//            return order.toString();
//        } catch (Exception e) {
//            e.printStackTrace();
//            return "error";
//        }
//    }

    @ResponseBody
//    @PostMapping("/confirm-payment")
    @RequestMapping(value = "/confirm-payment", method = {RequestMethod.GET, RequestMethod.POST})
    public String confirmPayment(@RequestParam int giftId, @RequestParam String paymentId, HttpSession session) {
        Gift gift = giftRepository.findById(giftId).orElseThrow();
//        gift.setPurchased(true);
        gift.setStatus("bought");

        String email = session.getAttribute("email").toString(); // adjust as needed
        User user = userRepository.findByEmail(email);
        gift.setBuyer(user);
//        gift.setPurchased(true);

        gift.setIsPurchased(true);
        giftRepository.save(gift);

        Transactions transactions = new Transactions();
        transactions.setEmail(email); // store email or name
        transactions.setBrand(gift.getBrand());
        transactions.setPrice(gift.getPrice());
        transactions.setAction("bought");
        transactions.setDateTime(LocalDateTime.now());

        transactionRepository.save(transactions);


//        Null value was assigned to a property [class com.Rewards.Swapify.model.Gift.isPurchased]
//        of primitive type: 'com.Rewards.Swapify.model.Gift.isPurchased' (setter)] with root cause
        // Save transaction
//        Transaction tx = new Transaction();
//        tx.setUsername(username);
//        tx.setBrand(gift.getBrand());
//        tx.setAction("bought");
//        tx.setPrice(gift.getPrice());
//        tx.setDateTime(LocalDateTime.now());
//        transactionRepository.save(tx);

        return "<script>alert('Voucher Bought! Check The PROMO CODE'); window.location.href='/myorders';</script>";
    }



    @PostMapping("/addMobileNumber")
    public String addMobileNumber(@RequestParam("mobilenumber") String mobilenumber, HttpSession session, HttpServletResponse response) throws IOException {

        PrintWriter out = response.getWriter();
        response.setContentType("text/html");

        try{

            User user = (User) session.getAttribute("user");

            if(user == null)
            {
                out.println("<script>alert('Error: Session has expired');window.location.href='/login.html';</script>");
            }
            user.setMobilenumber(mobilenumber);
            userRepository.save(user);
            out.println("<script>alert('Mobile Number Saved Successfully');window.location.href= 'account';</script>");
        }
        catch (Exception e)
        {

            System.out.println(e);
            out.println("<script>alert('Error: Mobile Number Not Saved'); window.location.href = 'account';</script>");
        }
        return null;
    }

    @GetMapping("/contact")
    public String contact()
    {
        return "contact.html";
    }

//    public String addbankDetails(@ModelAttribute BankDetails bankDetails, HttpSession session)
//    {
//        String email = (String) session.getAttribute("email");
//        User user = userRepository.findByEmail(email);
//
//        bankDetails.setUser(user);
//        bankDetailsRepo.save(bankDetails);
//        return "redirect:/homepage";
//    }

    @PostMapping("/contactUs")
    public String ContactUs(@ModelAttribute Contactus contactus, HttpServletResponse response, Model model) throws IOException {

        contactus.setName(contactus.getName().trim());
        contactus.setEmail(contactus.getEmail().trim());
        contactus.setSubject(contactus.getSubject().trim());
        contactus.setMessage(contactus.getMessage().trim());


        PrintWriter out = response.getWriter();

        try {
            model.addAttribute("name",contactus.getName());
            model.addAttribute("email",contactus.getEmail());
            model.addAttribute("subject",contactus.getSubject());
            model.addAttribute("UserMessage",contactus.getMessage());
            model.addAttribute("messages",contactus);
            contactRepository.save(contactus);
            response.setContentType("text/html");
            out.println("<script>alert('Message Sent Successfully'); window.location.href = '/contact.html';</script>");
        }

        catch (Exception e)
        {
            System.out.println(e);
            out.println("<script>alert('Error: Message Not Sent'); window.location.href = '/contact.html';</script>");
        }
        
        return null;
    }
}
