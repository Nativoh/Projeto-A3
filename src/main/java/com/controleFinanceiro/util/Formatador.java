package com.controleFinanceiro.util;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class Formatador {

    // locale brasileiro para moeda e números
    private static final Locale BRASIL = new Locale("pt", "BR");

    // padrão de data dd/MM/yyyy
    private static final DateTimeFormatter FMT_DATA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // construtor privado — classe utilitária, não instanciar
    private Formatador() {}

    // MOEDA

    public static String moeda(double valor) {
        NumberFormat fmt = NumberFormat.getCurrencyInstance(BRASIL);
        return fmt.format(valor);
    }

    // DATA

    public static String data(LocalDate data) {
        if (data == null) return "";
        return data.format(FMT_DATA);
    }

    // PERCENTUAL

    public static String percentual(double valor) {
        return String.format(BRASIL, "%.1f%%", valor);
    }


    public static String percentual(double usado, double total) {
        if (total <= 0) return "0,0%";
        return percentual((usado / total) * 100);
    }

    // NÚMERO INTEIRO

    public static String numero(int valor) {
        return NumberFormat.getIntegerInstance(BRASIL).format(valor);
    }
}