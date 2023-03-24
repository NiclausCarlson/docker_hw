package controller;

import db.Users;
import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<String> addUser(@RequestParam(name = "userName") String userName) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(users.createUser(userName).toString());

    }

    @RequestMapping(value = "/users/add-amount", method = RequestMethod.GET)
    // Добавляем пользователю userId amount денег
    public ResponseEntity<String> addAmount(@RequestParam(name = "userId") String userId, @RequestParam(name = "amount") Integer amount) {
        try {
            users.addAmount(new ObjectId(userId), amount);
        } catch (final RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ex.getMessage());
        }
        return ResponseEntity.status(HttpStatus.OK)
                .body("Success");
    }

    @RequestMapping(value = "/users/check-stocks", method = RequestMethod.GET)
    // Смотрим акции пользователя
    public ResponseEntity<String> checkStocks(@RequestParam(name = "userId") String userId) {
        try {
            var user = users.getUser(new ObjectId(userId));
            StringBuilder result = new StringBuilder();
            for (var stock : user.getStocks().entrySet()) {
                result.append("CompanyId: ")
                        .append(stock.getKey())
                        .append(" count: ")
                        .append(stock.getValue());
            }
            return ResponseEntity.status(HttpStatus.OK)
                    .body(result.toString());
        } catch (final RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ex.getMessage());
        }
    }

    @RequestMapping(value = "/users/how-much-money", method = RequestMethod.GET)
    public ResponseEntity<String> howMuchMoney(@RequestParam(name = "userId") String userId) {
        try {
            var user = users.getUser(new ObjectId(userId));
            Double result = 0.;
            for (var stock : user.getStocks().entrySet()) {
                result += stock.getValue();
            }
            return ResponseEntity.status(HttpStatus.OK)
                    .body(result.toString());
        } catch (final RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ex.getMessage());
        }
    }

    @RequestMapping(value = "/users/buy-stocks", method = RequestMethod.GET)
    // Пользователь userId покупает акции companyId на весь amount
    // Предполагается примерно следующий сценарий работы: из приложения идёт запрос в биржу за ценами акций
    // на основании этой информации пользователь решает сколько он будет покупать.
    // Далее происходит запрос в биржу с числом акций и ценой, за которую пользователь согласен произвести покупку
    // Если не получилось(на бирже нет столько акций или цена изменилась), то цикл повторяется
    public String buyStocks(@RequestParam(name = "userId") String userId,
                            @RequestParam(name = "companyId") String companyId,
                            @RequestParam(name = "count") Integer count,
                            @RequestParam(name = "price_per_unite") Double price_per_unite) {
        return "";
    }

    @RequestMapping(value = "/users/sell-stocks", method = RequestMethod.GET)
    // Пользователь userId продаёт count акций компании companyId
    // Пользователь пытается продать count акций за price_per_unite.
    // Если цена изменилась, то продать не получится(например, если цена уменьшилась, то
    // пользователь будет недоволен).
    public String sellStocks(@RequestParam(name = "userId") String userId,
                             @RequestParam(name = "companyId") String companyId,
                             @RequestParam(name = "count") Integer count,
                             @RequestParam(name = "price_per_unite") Double price_per_unite) {
        return "";
    }

}
