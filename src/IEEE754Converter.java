import java.util.Scanner;

/**
 * IEEE754Converter
 *
 * Single program that:
 * - Reads a numeric value (integer or floating, positive or negative)
 * - Prints step-by-step conversion into IEEE 754 single-precision (32-bit)
 * - Handles normalization, bias, mantissa construction, rounding (round-to-nearest-even)
 * - Shows final 32-bit binary and hex representation
 *
 * Usage:
 *   javac IEEE754Converter.java
 *   java IEEE754Converter
 *
 * Enter a number when prompted: e.g. -1234.5678  or  12345  or 0.15625
 */
public class IEEE754Converter {

    @SuppressWarnings("resource")
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter a number (integer or floating, positive or negative): ");
        String input = sc.nextLine().trim();
        if (input.isEmpty()) {
            System.out.println("No input provided. Exiting.");
            return;
        }

        double value;
        try {
            value = Double.parseDouble(input);
        } catch (NumberFormatException e) {
            System.out.println("Invalid number format.");
            return;
        }

        System.out.println("\nInput value: " + value);
        System.out.println("---- Step-by-step IEEE 754 (single precision) conversion ----\n");

        // Special cases
        if (Double.isNaN(value)) {
            System.out.println("Value is NaN (not a number). IEEE 754 single representation is implementation defined.");
            return;
        }
        if (Double.isInfinite(value)) {
            System.out.println("Value is " + (value > 0 ? "+" : "-") + "Infinity");
            int sign = value < 0 ? 1 : 0;
            String exp = "11111111";
            String mant = "00000000000000000000000";
            System.out.printf("Result: %d %s %s  (hex: 0x%08X)\n", sign, exp, mant,
                              (int)(((sign<<31) | (0xFF << 23))));
            return;
        }

        // Handle zero specially to avoid later issues
        if (value == 0.0) {
            int sign = (Double.doubleToRawLongBits(value) >>> 63) == 1 ? 1 : 0;
            String exp = "00000000";
            String mant = "00000000000000000000000";
            System.out.printf("Zero detected. Sign=%d\nResult: %d %s %s (hex 0x%08X)\n",
                              sign, sign, exp, mant, (sign<<31));
            return;
        }

        // 1) Sign bit
        int signBit = value < 0 ? 1 : 0;
        double absVal = Math.abs(value);
        System.out.println("1) Sign bit:");
        System.out.println("   value is " + (signBit == 1 ? "negative" : "positive") + " -> Sign = " + signBit + "\n");

        // 2) Separate integer and fractional parts
        long integerPart = (long) Math.floor(absVal);
        double fractionalPart = absVal - integerPart;
        System.out.println("2) Split into integer and fractional parts:");
        System.out.println("   Integer part (decimal) = " + integerPart);
        System.out.println("   Fractional part (decimal) = " + fractionalPart + "\n");

        // 3) Integer part to binary
        String intBin;
        if (integerPart == 0) {
            intBin = "0";
        } else {
            intBin = Long.toBinaryString(integerPart);
        }
        System.out.println("3) Integer part to binary:");
        System.out.println("   Integer binary = " + intBin + "\n");

        // 4) Fractional part to binary (generate many bits to ensure correct rounding)
        StringBuilder fracBinBuilder = new StringBuilder();
        double frac = fractionalPart;
        int maxFracBits = 60; // produce plenty of bits for safe rounding decisions
        System.out.println("4) Fractional part to binary (multiplication by 2):");
        for (int i = 1; i <= maxFracBits; i++) {
            frac *= 2.0;
            if (frac >= 1.0) {
                fracBinBuilder.append('1');
                frac -= 1.0;
            } else {
                fracBinBuilder.append('0');
            }
            // print first several steps for clarity
            if (i <= 20) {
                System.out.printf("   Step %2d: bit=%c, remaining frac=%.12f\n", i, fracBinBuilder.charAt(i-1), frac);
            } else if (i == 21) {
                System.out.println("   ... (fractional conversion continued to " + maxFracBits + " bits for rounding)");
            }
            if (frac == 0.0) {
                // exact fractional representation reached
                System.out.println("   Fractional part terminated exactly after " + i + " bits.");
                break;
            }
        }
        String fracBin = fracBinBuilder.toString();
        System.out.println("\n   Fractional binary (first 60 bits or until termination) = " +
                           (fracBin.length() > 0 ? "0." + fracBin : "0.0"));
        System.out.println();

        // 5) Combine integer and fractional binary into one representation
        String combined;
        if (!intBin.equals("0")) {
            combined = intBin + "." + fracBin;
        } else {
            combined = "0." + fracBin;
        }
        System.out.println("5) Combined binary representation:");
        System.out.println("   " + absVal + " = " + combined + " (binary)\n");

        // 6) Normalize to 1.x * 2^E
        int exponent; // actual exponent
        String mantissaBits = ""; // bits after the leading 1
        if (!intBin.equals("0")) {
            // If integer part non-zero, shift point left (len-1)
            exponent = intBin.length() - 1;
            // mantissa is intBin without leading 1 + fractional bits
            mantissaBits = intBin.substring(1) + fracBin;
            System.out.println("6) Normalization (integer part non-zero):");
            System.out.println("   Normalized form: 1." + mantissaBits + " * 2^" + exponent);
        } else {
            // integer part zero: need to find first '1' in fractional bits
            int firstOneIndex = fracBin.indexOf('1');
            if (firstOneIndex == -1) {
                // somehow fractional zero (should be zero earlier)
                exponent = 0;
                mantissaBits = "0";
                System.out.println("6) Normalization: no '1' found in fractional bits -> pure zero (handled earlier).");
            } else {
                exponent = -(firstOneIndex + 1);
                // mantissa is bits after that first 1
                mantissaBits = fracBin.substring(firstOneIndex + 1);
                System.out.println("6) Normalization (integer part is zero):");
                System.out.println("   First 1 in fractional part at position " + (firstOneIndex+1));
                System.out.println("   Normalized form: 1." + mantissaBits + " * 2^" + exponent);
            }
        }
        System.out.println();

        // 7) Biased exponent
        int biasedExponent = exponent + 127;
        System.out.println("7) Exponent and bias:");
        System.out.println("   Actual exponent = " + exponent);
        System.out.println("   Biased exponent = exponent + 127 = " + biasedExponent);

        String expBits;
        boolean isSubnormal = false;
        if (biasedExponent <= 0) {
            // Subnormal number (de-normal): exponent field all zeros, mantissa is shifted
            isSubnormal = true;
            expBits = "00000000";
            System.out.println("   Biased exponent <= 0 -> subnormal (de-normal) representation needed.");
        } else if (biasedExponent >= 255) {
            // overflow to Infinity
            expBits = "11111111";
            System.out.println("   Biased exponent >= 255 -> overflow (infinity) (not likely for typical inputs).");
        } else {
            expBits = String.format("%8s", Integer.toBinaryString(biasedExponent)).replace(' ', '0');
            System.out.println("   Exponent bits (8-bit) = " + expBits);
        }
        System.out.println();

        // 8) Construct mantissa (23 bits) with rounding (round-to-nearest-even)
        String mantissa23;
        if (isSubnormal) {
            // For subnormal, exponent bits = 0, mantissa is the leading zeros then the significant bits without an implicit leading 1.
            // We must shift the mantissa right by (1 - biasedExponent) places.
            int shift = 1 - biasedExponent; // positive
            // create a long mantissa source: leading zeros then intBin + fracBin
            String fullBits = (intBin.equals("0") ? "" : intBin) + fracBin;
            // For subnormal, we do NOT assume implicit leading 1, so we take from the full bits starting at position 'shift'
            // Build mantissa source with many zeros prefix to capture shift positions
            StringBuilder source = new StringBuilder();
            for (int i = 0; i < shift; i++) source.append('0');
            source.append(fullBits);
            // now we need first 23 bits of source as mantissa, and use rest for rounding
            String sourceStr = source.toString();
            if (sourceStr.length() < 23) sourceStr = String.format("%-23s", sourceStr).replace(' ', '0');
            String m = sourceStr.substring(0, Math.min(23, sourceStr.length()));
            String remainder = sourceStr.length() > 23 ? sourceStr.substring(23) : "";
            // rounding
            char guard = remainder.length() > 0 ? remainder.charAt(0) : '0';
            boolean sticky = remainder.length() > 1 && remainder.substring(1).contains("1");
            mantissa23 = roundMantissa(m, guard, sticky);
        } else {
            // Normal case: implicit leading 1, mantissa is bits after leading 1
            String mantissaSource = mantissaBits; // may be shorter than needed
            // ensure we have at least 24 bits (23 mantissa + guard) by padding zeros
            StringBuilder sb = new StringBuilder(mantissaSource);
            while (sb.length() < 50) sb.append('0'); // plenty for guard and sticky calculation
            String m23 = sb.substring(0, Math.min(23, sb.length()));
            String remainder = sb.length() > 23 ? sb.substring(23) : "";
            char guard = remainder.length() > 0 ? remainder.charAt(0) : '0';
            boolean sticky = remainder.length() > 1 && remainder.substring(1).contains("1");
            System.out.println("8) Mantissa (before rounding):");
            System.out.println("   Mantissa source (bits after leading 1) = " + mantissaSource);
            System.out.println("   Taking first 23 bits as mantissa = " + m23);
            System.out.println("   Guard bit = " + guard + ", Sticky (OR of remaining bits) = " + sticky);
            mantissa23 = roundMantissa(m23, guard, sticky);
            // after rounding we may need to adjust exponent if mantissa overflowed
            if (mantissa23.length() > 23) {
                // rounding caused carry that makes mantissa 24 bits ("1" + 23 zeros)
                // drop the leading 1 and increment exponent
                mantissa23 = mantissa23.substring(1); // now 23 bits
                biasedExponent += 1;
                if (biasedExponent >= 255) {
                    // overflow to infinity
                    expBits = "11111111";
                    mantissa23 = "00000000000000000000000";
                    System.out.println("   Rounding caused exponent overflow -> Infinity");
                } else {
                    expBits = String.format("%8s", Integer.toBinaryString(biasedExponent)).replace(' ', '0');
                    System.out.println("   Rounding caused mantissa carry -> incremented biased exponent to " + biasedExponent +
                                       ", new exponent bits = " + expBits);
                }
            }
        }

        // If not subnormal and not overflow, ensure expBits set
        if (!isSubnormal && biasedExponent > 0 && biasedExponent < 255) {
            expBits = String.format("%8s", Integer.toBinaryString(biasedExponent)).replace(' ', '0');
        }

        // Ensure mantissa23 length = 23 (if not subnormal rounding branch already handled)
        if (mantissa23.length() > 23) mantissa23 = mantissa23.substring(mantissa23.length()-23);
        if (mantissa23.length() < 23) mantissa23 = mantissa23 + "0".repeat(23 - mantissa23.length());

        // 9) Final 32-bit combination
        String finalBits = String.valueOf(signBit) + expBits + mantissa23;
        System.out.println("\n9) Final fields:");
        System.out.println("   Sign = " + signBit);
        System.out.println("   Exponent (biased) bits = " + expBits + "  (decimal = " + biasedExponent + ")");
        System.out.println("   Mantissa (23 bits) = " + mantissa23);
        System.out.println("\nFinal 32-bit IEEE 754 representation:");
        System.out.println("   " + finalBits);
        // Hex representation
        int asInt = (int) Long.parseLong(finalBits, 2);
        System.out.printf("   Hex: 0x%08X\n", asInt);

        // 10) Verification: use Java's Float.floatToIntBits
        float asFloat = (float) value;
        int builtin = Float.floatToIntBits(asFloat);
        String builtinBits = String.format("%32s", Integer.toBinaryString(builtin)).replace(' ', '0');
        System.out.println("\nVerification using Java's Float.floatToIntBits(float):");
        System.out.println("   float value (float cast) = " + asFloat);
        System.out.println("   Java bits = " + builtinBits + "  (hex 0x" + Integer.toHexString(builtin).toUpperCase() + ")");
        System.out.println("\nNote: slight differences may occur due to rounding when casting double->float.");

        sc.close();
    }

    /**
     * Round mantissa (binary string m) based on guard and sticky, using round-to-nearest-even.
     * Returns either a 23-bit string, or 24-bit if carry overflows (leading 1 then 23 bits).
     */
    private static String roundMantissa(String m, char guard, boolean sticky) {
        // m is a sequence of bits representing the mantissa candidate (length may be <= 23)
        // We apply round-to-nearest-even:
        // - If guard==0 => round down (no change)
        // - If guard==1:
        //    * if sticky==1 -> round up
        //    * if sticky==0 -> round to even: if LSB of m == '1' -> round up; else round down
        StringBuilder mant = new StringBuilder(m);
        while (mant.length() < 23) mant.append('0'); // pad if short
        boolean roundUp = false;
        if (guard == '1') {
            if (sticky) roundUp = true;
            else {
                // sticky == 0 => tie: round to even
                if (mant.charAt(mant.length() - 1) == '1') roundUp = true;
            }
        }

        if (!roundUp) {
            return mant.toString();
        } else {
            // add 1 to the mantissa binary (lsb at end)
            int carry = 1;
            for (int i = mant.length() - 1; i >= 0 && carry == 1; i--) {
                char c = mant.charAt(i);
                if (c == '0') {
                    mant.setCharAt(i, '1');
                    carry = 0;
                } else {
                    mant.setCharAt(i, '0');
                    carry = 1;
                }
            }
            if (carry == 1) {
                // overflow: mantissa became 100...0 (24 bits with leading 1)
                return "1" + mant.toString(); // caller will handle cropping and exponent increment
            } else {
                return mant.toString();
            }
        }
    }
}
