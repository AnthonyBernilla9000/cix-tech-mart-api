package org.istrfa.utils;

import java.text.DecimalFormat;
import java.util.Objects;

public class NumberUtils {

    private NumberUtils() {
    }

    // Constante para el formato por defecto
    private static final String DEFAULT_PATTERN = "#,###.00";

    /**
     * double as string
     * Quitar notación científica a números grandes y convertir a String
     *
     * @param number number
     * @return {@link String}
     * @see String
     */
    public static String doubleAsString(Double number) {
        return String.format("%.2f", number); // Quitar notación científica a números grandes y convertir a String
    }

    /**
     * Calculate percentage double.
     * Calcular porcentaje de un número
     *
     * @param porcentaje the porcentaje
     * @param numero     the numero
     * @return the double
     */
    public static Double calculatePercentage(Double porcentaje, Integer numero) {
        return (porcentaje / 100) * numero;
    }

    /**
     * Validate double string.
     * Validar que un número Double no sea null
     *
     * @param number the number
     * @return the string
     */
    public static String validateDouble(Double number) {
        if (Objects.isNull(number)) return "";
        else return number.toString();
    }

    public static String format(Double value, String pattern) {
        if (Objects.isNull(value)) return "";
        if (value == 0) return "00.00";

        // Crear una instancia de DecimalFormat con el patrón deseado
        DecimalFormat decimalFormat = new DecimalFormat(pattern);

        // Formatear el número usando DecimalFormat
        return decimalFormat.format(value);
    }

    public static String format(Double value) {
        // Utilizar el formato por defecto
        return format(value, DEFAULT_PATTERN);
    }
}

