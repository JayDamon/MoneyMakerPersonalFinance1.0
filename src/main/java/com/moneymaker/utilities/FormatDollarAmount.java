package com.moneymaker.utilities;

import com.moneymaker.modules.transactionmanager.transactions.Transaction;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;

/**
 * Created by Jay Damon on 9/17/2016.
 */
public class FormatDollarAmount {

    public String CleanDollarAmountsForSQL(String amount) {

        if (amount.contains("$")) {
            amount = amount.replace("$","");
        }
        if (amount.startsWith("(")) {
            amount = amount.replace("(","-");
            amount = amount.replace(")","");
        }
        if (amount.contains(",")) {
            amount = amount.replace(",","");
        }

        return amount;
    }
    
    public String FormatAsDollarWithParenthesis(String amount, String type) {
        BigDecimal bigAmount = new BigDecimal(amount).setScale(2, RoundingMode.CEILING);
        if (type.equals(new Transaction().EXPENSE)) {
            bigAmount.subtract(bigAmount.multiply(BigDecimal.valueOf(2)));
        }
        NumberFormat fmt = NumberFormat.getCurrencyInstance();
        amount = fmt.format(bigAmount);
        return amount;
    }

}
