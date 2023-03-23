package controller;

import db.Companies;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class Controllers {
    final Companies companies;

    public Controllers() {
        this.companies = new Companies();
    }

    @RequestMapping(value = "/burse/add-company", method = RequestMethod.GET)
    // Регистрируем компанию с названием companyName и объявляем торги, с ценой акции в price
    public String addCompany(@RequestParam(name = "companyName") String companyName,
                             @RequestParam(name = "stockPrice") Double price) {
        return "";
    }

    @RequestMapping(value = "/burse/add-stocks", method = RequestMethod.GET)
    // Добавляем компании count акций
    public String addStocks(@RequestParam(name = "companyId") String companyId,
                            @RequestParam(name = "count") Integer count) {
        return "";
    }

    @RequestMapping(value = "/burse/get-stocks-info", method = RequestMethod.GET)
    // Возвращаем мапу <Компания, {цена за акцию, количество акций}>
    public String getStocksInfo() {
        return "";
    }

    @RequestMapping(value = "/burse/buy", method = RequestMethod.GET)
    // Покупаем акции на все деньги. В случае успеха возвращаем остаток и число купленных акций
    public String buy(@RequestParam(name = "companyId") String companyId,
                      @RequestParam(name = "amount") Double amount
    ) {
        return "";
    }
}
