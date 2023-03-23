package controller;

import db.Users;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class Controllers {
    final Users users;

    public Controllers() {
        this.users = new Users();
    }

    @RequestMapping(value = "/users/add-user", method = RequestMethod.GET)
    // Создаём пользователя и возвращаем его Id
    public String addUser(@RequestParam(name = "userName") String userName) {
        return "";
    }

    @RequestMapping(value = "/users/add-amount", method = RequestMethod.GET)
    // Добавляем пользователю userId amount денег
    public String addAmount(@RequestParam(name = "userId") String userId, @RequestParam(name = "amount") Integer amount) {
        return "";
    }

    @RequestMapping(value = "/users/check-stocks", method = RequestMethod.GET)
    // Смотрим акции пользователя
    public String checkStocks(@RequestParam(name = "userId") String userId) {
        return "";
    }

    @RequestMapping(value = "/users/how-much-money", method = RequestMethod.GET)
    public String howMuchMoney(@RequestParam(name = "userId") String userId) {
        return "";
    }

    @RequestMapping(value = "/users/buy-stocks", method = RequestMethod.GET)
    // Пользователь userId покупает акции companyId на весь amount
    public String buyStocks(@RequestParam(name = "userId") String userId,
                            @RequestParam(name = "companyId") String companyId,
                            @RequestParam(name = "amount") Double amount) {
        return "";
    }

    @RequestMapping(value = "/users/sell-stocks", method = RequestMethod.GET)
    // Пользователь userId продаёт count акций компании companyId
    public String sellStocks(@RequestParam(name = "userId") String userId,
                             @RequestParam(name = "companyId") String companyId,
                             @RequestParam(name = "count") Integer count) {
        return "";
    }

}
