package com.example.eventmanagement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Controller
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private RegistrationRepository registrationRepository;

    // ✅ Show Register Page
    @GetMapping("/register")
    public String showRegisterPage() {
        return "register";
    }

    // ✅ Register User
    @PostMapping("/register")
    public String registerUser(@RequestParam String name,
                               @RequestParam String email,
                               @RequestParam String password,
                               Model model) {

        User existingUser = userRepository.findByEmail(email);

        if(existingUser != null){
            model.addAttribute("error", "User already registered with this email!");
            return "register";
        }

        User user = new User();

        user.setName(name);
        user.setEmail(email);
        user.setPassword(password);
        user.setRole("STUDENT");

        userRepository.save(user);

        return "redirect:/login?success=true";
    }

    // ✅ Show Login Page
    @GetMapping("/login")
    public String showLoginPage(@RequestParam(required = false) String success,
                                Model model) {

        if(success != null){
            model.addAttribute("message", "Registration successful! Please login.");
        }

        return "login";
    }

    // ✅ Login Logic
    @PostMapping("/login")
    public String loginUser(@RequestParam String email,
                            @RequestParam String password,
                            Model model) {

        User user = userRepository.findByEmail(email);

        if (user == null) {
            model.addAttribute("error", "User not registered! Please register first.");
            return "login";
        }

        if (!user.getPassword().equals(password)) {
            model.addAttribute("error", "Incorrect password!");
            return "login";
        }

        if (user.getRole().equalsIgnoreCase("ADMIN")) {
            return "redirect:/admin/add-event";
        } else {
            return "redirect:/student/events?email=" + user.getEmail();
        }
    }
    // ================= ADMIN =================

    // ✅ Show Add Event Page
    @GetMapping("/admin/add-event")
    public String showAddEventPage(Model model) {

        List<Event> events = eventRepository.findAll();
        model.addAttribute("events", events);

        return "add-event";
    }

    // ✅ Add Event
    @PostMapping("/admin/add-event")
    public String addEvent(@RequestParam String name,
                           @RequestParam String category,
                           @RequestParam String venue,
                           @RequestParam String date,
                           @RequestParam String time,
                           @RequestParam double fee,
                           @RequestParam int maxSeats,
                           @RequestParam String imageUrl) {

        Event event = new Event(
                name,
                category,
                venue,
                LocalDate.parse(date),
                LocalTime.parse(time),
                fee,
                maxSeats,
                imageUrl
        );

        eventRepository.save(event);

        return "redirect:/admin/add-event";
    }

    // ================= STUDENT =================

    // ✅ View All Events
    @GetMapping("/student/events")
    public String viewEvents(@RequestParam String email,
                             @RequestParam(required = false) String success,
                             Model model) {

        List<Event> events = eventRepository.findAll();
        model.addAttribute("events", events);
        model.addAttribute("studentEmail", email);

        if (success != null) {
            model.addAttribute("message", "Successfully Registered!");
        }

        return "view-events";
    }

    // ✅ Register For Event

    @PostMapping("/student/register-event")
    public String registerEvent(@RequestParam Long eventId,
                                @RequestParam String email) {

        Event event = eventRepository.findById(eventId).orElse(null);
        User student = userRepository.findByEmail(email);

        if(event == null || student == null){
            return "redirect:/student/events?email=" + email;
        }

        // Check duplicate registration
        boolean alreadyRegistered =
                registrationRepository.existsByStudentEmailAndEventId(email, eventId);

        if(alreadyRegistered){
            return "redirect:/student/events?email=" + email + "&error=alreadyRegistered";
        }

        // Check seat availability
        if(event.getAvailableSeats() <= 0){
            return "redirect:/student/events?email=" + email + "&error=seatsFull";
        }

        // Register student
        Registration reg = new Registration(student.getEmail(), event);
        registrationRepository.save(reg);

        event.setAvailableSeats(event.getAvailableSeats() - 1);
        eventRepository.save(event);

        return "redirect:/student/events?email=" + email + "&success=true";
    }
    @GetMapping("/admin/registrations")
    public String viewRegistrations(Model model) {

        List<Registration> registrations = registrationRepository.findAll();
        model.addAttribute("registrations", registrations);

        return "admin-registrations";
    }
    @GetMapping("/student/my-events")
    public String myEvents(@RequestParam String email, Model model) {

        List<Registration> registrations =
                registrationRepository.findByStudentEmail(email);

        model.addAttribute("registrations", registrations);
        model.addAttribute("studentEmail", email);

        return "my-events";
    }
    @GetMapping("/student/payment")
    public String paymentPage(@RequestParam Long eventId,
                              @RequestParam String email,
                              Model model) {

        Event event = eventRepository.findById(eventId).orElse(null);

        model.addAttribute("event", event);
        model.addAttribute("email", email);

        return "payment";
    }
    @PostMapping("/student/payment-success")
    public String paymentSuccess(@RequestParam Long eventId,
                                 @RequestParam String email) {

        Event event = eventRepository.findById(eventId).orElse(null);
        User student = userRepository.findByEmail(email);

        if(event != null && student != null){

            boolean alreadyRegistered =
                    registrationRepository.existsByStudentEmailAndEventId(email, eventId);

            if(alreadyRegistered){
                return "redirect:/student/events?email=" + email + "&error=alreadyRegistered";
            }

            if(event.getAvailableSeats() > 0){

                Registration registration =
                        new Registration(student.getEmail(), event);

                registrationRepository.save(registration);

                event.setAvailableSeats(event.getAvailableSeats() - 1);
                eventRepository.save(event);
            }
        }

        return "redirect:/student/events?email=" + email + "&success=true";
    }
    @GetMapping("/admin/delete-event/{id}")
    public String deleteEvent(@PathVariable Long id) {

        eventRepository.deleteById(id);

        return "redirect:/admin/add-event";
    }
}