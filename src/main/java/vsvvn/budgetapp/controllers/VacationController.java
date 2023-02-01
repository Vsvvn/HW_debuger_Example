package vsvvn.budgetapp.controllers;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vsvvn.budgetapp.services.BudgetService;

@RestController
@RequestMapping("/vacation")
public class VacationController {

   private final BudgetService budgetService;

    public VacationController(BudgetService budgetService) {
        this.budgetService = budgetService;
    }

    @GetMapping
    public int vacationBonus(@RequestParam int vacationDays) {

        return budgetService.getVacationBonus(vacationDays);
    }

    @GetMapping("/salary")
    public int salaryWithVacation(@RequestParam int vacationDays, @RequestParam int workingDays, @RequestParam int vacWorkDays) {
        return budgetService.getSalaryWithVacation(vacationDays, vacWorkDays, workingDays);
    }
}
