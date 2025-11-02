# 🧮 IEEE 754 Converter in Java

> **A comprehensive IEEE 754 Floating Point Converter built with Java**
> Converts any positive or negative number (integer or floating) into its 32-bit IEEE 754 binary and hexadecimal form — with **step-by-step explanations**.

---

## 🚀 **Overview**

This Java-based tool allows students and professionals to understand how **floating-point numbers** are represented in **IEEE 754 single-precision (32-bit)** format.
It not only provides the **final binary result** but also walks you through every computational step:

- ✅ Conversion of integer and fractional parts to binary
- ✅ Normalization of binary number
- ✅ Calculation of biased exponent
- ✅ Mantissa extraction and rounding (round-to-nearest-even)
- ✅ Generation of 32-bit binary and hexadecimal output
- ✅ Verification using Java’s built-in `Float.floatToIntBits()`

---

## 🧰 **Features**

| Feature                        | Description                                                               |
| ------------------------------ | ------------------------------------------------------------------------- |
| 🎯 **Universal Input**         | Accepts any decimal value — integer, floating, positive, or negative.     |
| 📊 **Step-by-Step Conversion** | Displays every IEEE 754 calculation step in an easy-to-read format.       |
| ⚙️ **Accurate Rounding**       | Implements IEEE’s _Round to Nearest Even_ rule.                           |
| 🧠 **Educational Design**      | Ideal for students learning number systems and floating-point standards.  |
| 💻 **Cross-Platform**          | Runs perfectly on Windows, macOS, and Linux using any Java-supported IDE. |

---

## 🧩 **How This Tool Works**

The program performs these steps automatically once you enter a number:

1. **Sign Detection:** Determines the sign bit (1 for negative, 0 for positive).
2. **Integer Conversion:** Converts the integer part of the number into binary.
3. **Fractional Conversion:** Converts the fraction by multiplying by 2 and capturing integer parts.
4. **Normalization:** Adjusts the binary number to scientific notation form (1.x × 2^E).
5. **Exponent Calculation:** Adds 127 to the exponent (biasing).
6. **Mantissa Extraction:** Captures 23 bits after the binary point.
7. **Rounding:** Applies _round-to-nearest-even_ rule for precision.
8. **Final Output:** Displays 32-bit IEEE 754 representation (binary + hexadecimal).

---

## 🧑‍💻 **Project Structure**

```bash
IEEE754Converter-Java/
│
├── src/
│ └── IEEE754Converter.java # Main program file
│
├── README.md # Project documentation (this file)
├── .gitignore # Ignore compiled & IDE files
└── LICENSE # MIT License for open sharing
```

---

## 🧠 **How to Run the Program**

### 🖥️ **Option 1: Run in VS Code**

1. Install the **Java Extension Pack** from VS Code Marketplace.
2. Open the folder `IEEE754Converter-Java` in VS Code.
3. Open the file `IEEE754Converter.java`.
4. Press `Ctrl + F5` (or click “Run Java”).
5. Enter any number, e.g. `-3105.8565`, and view the detailed conversion output.

---

### 💡 **Option 2: Run in IntelliJ IDEA**

1. Open IntelliJ → `File > New > Project from Existing Sources`
2. Select the folder `IEEE754Converter-Java`
3. Mark `src` as a **Source Root**
4. Right-click `IEEE754Converter.java` → **Run 'IEEE754Converter.main()'**

---

### 🧩 **Option 3: Run in Eclipse**

1. Create a **New Java Project** named `IEEE754Converter-Java`.
2. Copy `IEEE754Converter.java` into the `src` folder.
3. Right-click the file → **Run As → Java Application**.

---

### 🧱 **Option 4: Run Using Command Line**

If you have **JDK installed**:

```bash
cd path/to/IEEE754Converter-Java/src
javac IEEE754Converter.java
java IEEE754Converter
```

---

### 🧮 Example Input:

```
Enter a number: -3105.8565
```

---

### 🧮 Example Output:

```
Sign bit = 1
Integer binary = 110000100001
Fractional binary = .1101101101...
Normalized form = 1.10000100001110110110100 × 2^11
Exponent (biased) = 138 (10001010)
Mantissa = 10000100001110110110100
Final 32-bit IEEE 754 = 11000101010000100001110110110100
Hexadecimal = 0xC5421DB4
```
