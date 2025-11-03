package org.delcom.starter.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Locale; 

import static org.junit.jupiter.api.Assertions.*;

class HomeControllerTest {

    private HomeController homeController;

    @BeforeEach
    void setUp() {
        homeController = new HomeController();
    }

    private String encodeBase64(final String text) {
        return Base64.getEncoder().encodeToString(text.getBytes(StandardCharsets.UTF_8));
    }

    // --- Tes Asli ---

    @Test
    @DisplayName("Mengembalikan pesan selamat datang yang benar")
    void hello_ShouldReturnWelcomeMessage() {
        final String responseBody = homeController.hello();
        assertEquals("Hay Abdullah, selamat datang di pengembangan aplikasi dengan Spring Boot!", responseBody);
    }

    @Test
    @DisplayName("Mengembalikan pesan sapaan yang dipersonalisasi")
    void helloWithName_ShouldReturnPersonalizedGreeting() {
        final String name = "Abdullah";
        final String responseBody = homeController.sayHello(name);
        assertEquals("Hello, " + name + "!", responseBody);
    }

    // --- Tes untuk informasiNim ---

    @Test
    @DisplayName("informasiNim - NIM Valid (11S)")
    void informasiNim_Valid() {
        final String testNim = "11S24001";
        final String expectedOutput = """
                Inforamsi NIM 11S24001:\s
                >> Program Studi: Sarjana Informatika
                >> Angkatan: 2024
                >> Urutan: 1""";

        final ResponseEntity<String> apiResponse = homeController.informasiNim(testNim);

        assertEquals(HttpStatus.OK, apiResponse.getStatusCode());
        assertEquals(expectedOutput, apiResponse.getBody());
    }

    @Test
    @DisplayName("informasiNim - NIM Panjang Tidak Valid")
    void informasiNim_InvalidLength() {
        final String testNim = "11S24";
        final String expectedOutput = "Format NIM tidak valid. Harap masukkan 8 digit.";
        final ResponseEntity<String> apiResponse = homeController.informasiNim(testNim);
        assertEquals(HttpStatus.BAD_REQUEST, apiResponse.getStatusCode());
        assertEquals(expectedOutput, apiResponse.getBody());
    }

    @Test
    @DisplayName("informasiNim - Prefix NIM Tidak Dikenal")
    void informasiNim_InvalidPrefix() {
        final String testNim = "99S24001";
        final String expectedOutput = "Prefix NIM '99S' tidak ditemukan.";
        final ResponseEntity<String> apiResponse = homeController.informasiNim(testNim);
        assertEquals(HttpStatus.BAD_REQUEST, apiResponse.getStatusCode());
        assertEquals(expectedOutput, apiResponse.getBody());
    }

    @Test
    @DisplayName("informasiNim - Input Parse Error (Memicu Catch)")
    void informasiNim_InvalidParse() {
        final String testNim = "11SXX001";
        final ResponseEntity<String> apiResponse = homeController.informasiNim(testNim);
        assertEquals(HttpStatus.BAD_REQUEST, apiResponse.getStatusCode());
        assertTrue(apiResponse.getBody().contains("For input string: \"XX\""));
    }

    // --- Tes untuk perolehanNilai ---

    @Test
    @DisplayName("perolehanNilai - Skenario Kalkulasi Lengkap (Grade A) dengan baris kosong")
    void perolehanNilai_FullScenario() {
        // PERHATIKAN: Ada baris kosong setelah PA|100|80 untuk menguji coverage baris kosong
        final String testInputData = String.join("\n",
                "10 15 10 15 20 30",
                "PA|100|80", 
                "", 
                "T|100|90", "K|100|85", "P|100|95", "UTS|100|75", "UAS|100|88", "---"
        );
        final String encodedInput = encodeBase64(testInputData);

        final String expectedOutput = """
                Perolehan Nilai:
                >> Partisipatif: 80/100 (8.00/10)
                >> Tugas: 90/100 (13.50/15)
                >> Kuis: 85/100 (8.50/10)
                >> Proyek: 95/100 (14.25/15)
                >> UTS: 75/100 (15.00/20)
                >> UAS: 88/100 (26.40/30)

                >> Nilai Akhir: 85.65
                >> Grade: A""";

        final ResponseEntity<String> apiResponse = homeController.perolehanNilai(encodedInput);
        assertEquals(HttpStatus.OK, apiResponse.getStatusCode());
        assertEquals(expectedOutput, apiResponse.getBody());
    }

    @Test
    @DisplayName("perolehanNilai - Skenario Grade Boundary A (>=79.5) - DIBULATKAN")
    void perolehanNilai_GradeA_Boundary() {
        // Rata-rata 79.5 (159/200 = 79.5), dibulatkan menjadi 80 untuk tiap komponen.
        // Final Score: 0.8 * 100 = 80.00
        final String testInputData = String.join("\n", "10 15 10 15 20 30", 
                                                    "PA|200|159", "T|200|159", "K|200|159", 
                                                    "P|200|159", "UTS|200|159", "UAS|200|159", "---"); 
        final String encodedInput = encodeBase64(testInputData);
        final ResponseEntity<String> apiResponse = homeController.perolehanNilai(encodedInput);
        final String responseBody = apiResponse.getBody();
        
        assertEquals(HttpStatus.OK, apiResponse.getStatusCode());
        assertTrue(responseBody.contains(">> Partisipatif: 80/100 (8.00/10)")); 
        assertTrue(responseBody.contains(">> Nilai Akhir: 80.00")); 
        assertTrue(responseBody.contains(">> Grade: A"));
    }
    
    @Test
    @DisplayName("perolehanNilai - Skenario Grade Boundary AB (<79.5)")
    void perolehanNilai_GradeAB_Boundary() {
        // Rata-rata 79 (dibawah 79.5)
        final String testInputData = String.join("\n", "10 15 10 15 20 30", 
                                                    "PA|100|79", "T|100|79", "K|100|79", 
                                                    "P|100|79", "UTS|100|79", "UAS|100|79", "---"); 
        final String encodedInput = encodeBase64(testInputData);
        final ResponseEntity<String> apiResponse = homeController.perolehanNilai(encodedInput);
        final String responseBody = apiResponse.getBody();
        
        assertEquals(HttpStatus.OK, apiResponse.getStatusCode());
        assertTrue(responseBody.contains(">> Nilai Akhir: 79.00")); 
        assertTrue(responseBody.contains(">> Grade: AB"));
    }

    @Test
    @DisplayName("perolehanNilai - Skenario Grade AB")
    void perolehanNilai_GradeAB() {
        final String testInputData = String.join("\n", "10 15 10 15 20 30", "PA|100|75", "T|100|75", "K|100|75", "P|100|75", "UTS|100|75", "UAS|100|75", "---");
        final String encodedInput = encodeBase64(testInputData);
        final ResponseEntity<String> apiResponse = homeController.perolehanNilai(encodedInput);
        final String responseBody = apiResponse.getBody();
        assertEquals(HttpStatus.OK, apiResponse.getStatusCode());
        assertTrue(responseBody.contains(">> Nilai Akhir: 75.00"));
        assertTrue(responseBody.contains(">> Grade: AB"));
    }

    @Test
    @DisplayName("perolehanNilai - Skenario Grade B")
    void perolehanNilai_GradeB() {
        final String testInputData = String.join("\n", "10 15 10 15 20 30", "PA|100|65", "T|100|65", "K|100|65", "P|100|65", "UTS|100|65", "UAS|100|65", "---");
        final String encodedInput = encodeBase64(testInputData);
        final ResponseEntity<String> apiResponse = homeController.perolehanNilai(encodedInput);
        final String responseBody = apiResponse.getBody();
        assertEquals(HttpStatus.OK, apiResponse.getStatusCode());
        assertTrue(responseBody.contains(">> Nilai Akhir: 65.00"));
        assertTrue(responseBody.contains(">> Grade: B"));
    }
    
    @Test
    @DisplayName("perolehanNilai - Skenario Grade BC")
    void perolehanNilai_GradeBC() {
        final String testInputData = String.join("\n", "10 15 10 15 20 30", "PA|100|60", "T|100|60", "K|100|60", "P|100|60", "UTS|100|60", "UAS|100|60", "---");
        final String encodedInput = encodeBase64(testInputData);
        final ResponseEntity<String> apiResponse = homeController.perolehanNilai(encodedInput);
        final String responseBody = apiResponse.getBody();
        assertEquals(HttpStatus.OK, apiResponse.getStatusCode());
        assertTrue(responseBody.contains(">> Nilai Akhir: 60.00"));
        assertTrue(responseBody.contains(">> Grade: BC"));
    }

    @Test
    @DisplayName("perolehanNilai - Skenario Grade C")
    void perolehanNilai_GradeC() {
        final String testInputData = String.join("\n", "10 15 10 15 20 30", "PA|100|50", "T|100|50", "K|100|50", "P|100|50", "UTS|100|50", "UAS|100|50", "---");
        final String encodedInput = encodeBase64(testInputData);
        final ResponseEntity<String> apiResponse = homeController.perolehanNilai(encodedInput);
        final String responseBody = apiResponse.getBody();
        assertEquals(HttpStatus.OK, apiResponse.getStatusCode());
        assertTrue(responseBody.contains(">> Nilai Akhir: 50.00"));
        assertTrue(responseBody.contains(">> Grade: C"));
    }

    @Test
    @DisplayName("perolehanNilai - Skenario Grade D")
    void perolehanNilai_GradeD() {
        final String testInputData = String.join("\n", "10 15 10 15 20 30", "PA|100|40", "T|100|40", "K|100|40", "P|100|40", "UTS|100|40", "UAS|100|40", "---");
        final String encodedInput = encodeBase64(testInputData);
        final ResponseEntity<String> apiResponse = homeController.perolehanNilai(encodedInput);
        final String responseBody = apiResponse.getBody();
        assertEquals(HttpStatus.OK, apiResponse.getStatusCode());
        assertTrue(responseBody.contains(">> Nilai Akhir: 40.00"));
        assertTrue(responseBody.contains(">> Grade: D"));
    }

    @Test
    @DisplayName("perolehanNilai - Skenario Input Jarang (Sparse)")
    void perolehanNilai_SparseInput() {
        final String testInputData = String.join("\n", "10 15 10 15 20 30", "T|100|90", "UTS|100|50", "---");
        final String encodedInput = encodeBase64(testInputData);
        final ResponseEntity<String> apiResponse = homeController.perolehanNilai(encodedInput);
        final String responseBody = apiResponse.getBody();
        assertEquals(HttpStatus.OK, apiResponse.getStatusCode());
        assertTrue(responseBody.contains(">> Partisipatif: 0/100 (0.00/10)"));
        assertTrue(responseBody.contains(">> Kuis: 0/100 (0.00/10)"));
        assertTrue(responseBody.contains(">> Nilai Akhir: 23.50"));
        assertTrue(responseBody.contains(">> Grade: E"));
    }

    @Test
    @DisplayName("perolehanNilai - Skenario Simbol Tidak Valid")
    void perolehanNilai_InvalidSymbol() {
        final String testInputData = String.join("\n", "10 15 10 15 20 30", "PA|100|80", "XYZ|100|100", "---");
        final String encodedInput = encodeBase64(testInputData);
        final ResponseEntity<String> apiResponse = homeController.perolehanNilai(encodedInput);
        final String responseBody = apiResponse.getBody();
        assertEquals(HttpStatus.OK, apiResponse.getStatusCode());
        assertTrue(responseBody.contains(">> Nilai Akhir: 8.00"));
    }

    @Test
    @DisplayName("perolehanNilai - Skenario Input Data Kosong (Hanya Bobot dan line break) - Memicu OK")
    void perolehanNilai_InputDataKosong() {
        final String testInputData = "10 15 10 15 20 30\n"; 
        final String encodedInput = encodeBase64(testInputData);
        
        final ResponseEntity<String> apiResponse = homeController.perolehanNilai(encodedInput);
        final String responseBody = apiResponse.getBody();

        assertEquals(HttpStatus.OK, apiResponse.getStatusCode());
        assertTrue(responseBody.contains(">> Nilai Akhir: 0.00"));
        assertTrue(responseBody.contains(">> Grade: E"));
    }
    
    // TEST UNTUK lines.length == 0
    @Test
    @DisplayName("perolehanNilai - Input Kosong Total (Memicu Exception)")
    void perolehanNilai_EmptyInput_TriggersException() {
        final String encodedInput = encodeBase64(""); // Input kosong
        final String expectedErrorMessage = "Format data input tidak valid atau tidak lengkap. Pastikan angka dan format sudah benar.";
        final ResponseEntity<String> apiResponse = homeController.perolehanNilai(encodedInput);
        assertEquals(HttpStatus.BAD_REQUEST, apiResponse.getStatusCode());
        assertEquals(expectedErrorMessage, apiResponse.getBody());
    }

    // TEST UNTUK lines[0].trim().isEmpty()
    @Test
    @DisplayName("perolehanNilai - Input Baris Bobot Kosong (Memicu Exception)")
    void perolehanNilai_EmptyWeightLine_TriggersException() {
        // Input Base64 dari string " \nPA|100|100". lines[0] adalah " ", yang trim() menjadi "".
        final String encodedInput = encodeBase64(" \nPA|100|100");
        final String expectedErrorMessage = "Format data input tidak valid atau tidak lengkap. Pastikan angka dan format sudah benar.";
        final ResponseEntity<String> apiResponse = homeController.perolehanNilai(encodedInput);
        assertEquals(HttpStatus.BAD_REQUEST, apiResponse.getStatusCode());
        assertEquals(expectedErrorMessage, apiResponse.getBody());
    }

    // TEST UNTUK weightTokens.length < 6
    @Test
    @DisplayName("perolehanNilai - Input Bobot Tidak Lengkap (Memicu Exception)")
    void perolehanNilai_IncompleteWeights_TriggersException() {
        final String encodedInput = encodeBase64("10 15 10 15 20"); // Hanya 5 bobot
        final String expectedErrorMessage = "Format data input tidak valid atau tidak lengkap. Pastikan angka dan format sudah benar.";
        final ResponseEntity<String> apiResponse = homeController.perolehanNilai(encodedInput);
        assertEquals(HttpStatus.BAD_REQUEST, apiResponse.getStatusCode());
        assertEquals(expectedErrorMessage, apiResponse.getBody());
    }

    // TEST UNTUK scoreParts.length < 3
    @Test
    @DisplayName("perolehanNilai - Input Nilai Detail Tidak Lengkap (Memicu Exception)")
    void perolehanNilai_IncompleteDetailScore_TriggersException() {
        final String testInputData = String.join("\n", 
                                "10 15 10 15 20 30", 
                                "PA|100", // Baris ini memicu scoreParts.length < 3
                                "---");
        final String encodedInput = encodeBase64(testInputData);
        final String expectedErrorMessage = "Format data input tidak valid atau tidak lengkap. Pastikan angka dan format sudah benar.";
        final ResponseEntity<String> apiResponse = homeController.perolehanNilai(encodedInput);
        assertEquals(HttpStatus.BAD_REQUEST, apiResponse.getStatusCode());
        assertEquals(expectedErrorMessage, apiResponse.getBody());
    }

    @Test
    @DisplayName("perolehanNilai - Input Malformed (Memicu Catch Block)")
    void perolehanNilai_InvalidInput() {
        final String encodedInput = encodeBase64("halo");
        final String expectedErrorMessage = "Format data input tidak valid atau tidak lengkap. Pastikan angka dan format sudah benar.";
        final ResponseEntity<String> apiResponse = homeController.perolehanNilai(encodedInput);
        assertEquals(HttpStatus.BAD_REQUEST, apiResponse.getStatusCode());
        assertEquals(expectedErrorMessage, apiResponse.getBody());
    }

    @Test
    @DisplayName("perolehanNilai - Input Base64 Tidak Valid")
    void perolehanNilai_InvalidBase64() {
        final String malformedBase64 = "!!INVALID!!";
        final String expectedErrorMessage = "Input Base64 tidak valid.";
        final ResponseEntity<String> apiResponse = homeController.perolehanNilai(malformedBase64);
        assertEquals(HttpStatus.BAD_REQUEST, apiResponse.getStatusCode());
        assertTrue(apiResponse.getBody().startsWith(expectedErrorMessage));
    }


    // --- Tes untuk perbedaanL ---

    @Test
    @DisplayName("perbedaanL - Matriks 3x3 (Ganjil, Dominan=Tengah)")
    void perbedaanL_Matrix3x3() {
        final String testInputData = String.join("\n", "3", "1 2 3", "4 5 6", "7 8 9");
        final String encodedInput = encodeBase64(testInputData);
        final String expectedOutput = """
                Nilai L: 20:
                Nilai Kebalikan L: 20
                Nilai Tengah: 5
                Perbedaan: 0
                Dominan: 5""";
        final ResponseEntity<String> apiResponse = homeController.perbedaanL(encodedInput);
        assertEquals(HttpStatus.OK, apiResponse.getStatusCode());
        assertEquals(expectedOutput, apiResponse.getBody());
    }
    
    @Test
    @DisplayName("perbedaanL - Matriks 4x4 (Genap, Dominan=L)")
    void perbedaanL_Matrix4x4() {
        final String testInputData = String.join("\n", "4", "1 2 3 4", "5 6 7 8", "9 10 11 12", "13 14 15 16");
        final String encodedInput = encodeBase64(testInputData);
        final String expectedOutput = """
                Nilai L: 57:
                Nilai Kebalikan L: 45
                Nilai Tengah: 34
                Perbedaan: 12
                Dominan: 57""";
        final ResponseEntity<String> apiResponse = homeController.perbedaanL(encodedInput);
        assertEquals(HttpStatus.OK, apiResponse.getStatusCode());
        assertEquals(expectedOutput, apiResponse.getBody());
    }

    @Test
    @DisplayName("perbedaanL - Matriks 1x1 (Edge Case)")
    void perbedaanL_Matrix1x1() {
        final String encodedInput = encodeBase64("1\n42");
        final String expectedOutput = """
                Nilai L: Tidak Ada
                Nilai Kebalikan L: Tidak Ada
                Nilai Tengah: 42
                Perbedaan: Tidak Ada
                Dominan: 42""";
        final ResponseEntity<String> apiResponse = homeController.perbedaanL(encodedInput);
        assertEquals(HttpStatus.OK, apiResponse.getStatusCode());
        assertEquals(expectedOutput, apiResponse.getBody());
    }

    @Test
    @DisplayName("perbedaanL - Matriks 2x2 (Edge Case)")
    void perbedaanL_Matrix2x2() {
        final String encodedInput = encodeBase64("2\n1 2\n3 4");
        final String expectedOutput = """
                Nilai L: Tidak Ada
                Nilai Kebalikan L: Tidak Ada
                Nilai Tengah: 10
                Perbedaan: Tidak Ada
                Dominan: 10""";
        final ResponseEntity<String> apiResponse = homeController.perbedaanL(encodedInput);
        assertEquals(HttpStatus.OK, apiResponse.getStatusCode());
        assertEquals(expectedOutput, apiResponse.getBody());
    }

    @Test
    @DisplayName("perbedaanL - Input Data Malformed (Memicu Catch)")
    void perbedaanL_InvalidInputData() {
        final String encodedInput = encodeBase64("abc");
        final String expectedErrorMessage = "Format data matriks tidak valid atau tidak lengkap.";
        final ResponseEntity<String> apiResponse = homeController.perbedaanL(encodedInput);
        assertEquals(HttpStatus.BAD_REQUEST, apiResponse.getStatusCode());
        assertEquals(expectedErrorMessage, apiResponse.getBody());
    }

    @Test
    @DisplayName("perbedaanL - Input Base64 Tidak Valid")
    void perbedaanL_InvalidBase64() {
        final String malformedBase64 = "!!INVALID!!";
        final String expectedErrorMessage = "Input Base64 tidak valid.";
        final ResponseEntity<String> apiResponse = homeController.perbedaanL(malformedBase64);
        
        assertEquals(HttpStatus.BAD_REQUEST, apiResponse.getStatusCode());
        assertTrue(apiResponse.getBody().startsWith(expectedErrorMessage));
    }
    
    // TEST UNTUK trimmedInput.isEmpty()
    @Test
    @DisplayName("perbedaanL - Input Data Kosong Total (Memicu Exception)")
    void perbedaanL_EmptyInput_TriggersException() {
        final String encodedInput = encodeBase64(""); // Input kosong
        final String expectedErrorMessage = "Format data matriks tidak valid atau tidak lengkap.";
        final ResponseEntity<String> apiResponse = homeController.perbedaanL(encodedInput);
        assertEquals(HttpStatus.BAD_REQUEST, apiResponse.getStatusCode());
        assertEquals(expectedErrorMessage, apiResponse.getBody());
    }
    
    // TEST UNTUK tokenIndex >= tokens.length
    @Test
    @DisplayName("perbedaanL - Data Matriks Tidak Lengkap (Memicu Exception)")
    void perbedaanL_IncompleteMatrixData_TriggersException() {
        // N=3, tapi hanya ada 8 angka (membutuhkan 9 angka)
        final String encodedInput = encodeBase64("3 1 2 3 4 5 6 7 8"); 
        final String expectedErrorMessage = "Format data matriks tidak valid atau tidak lengkap.";
        final ResponseEntity<String> apiResponse = homeController.perbedaanL(encodedInput);
        assertEquals(HttpStatus.BAD_REQUEST, apiResponse.getStatusCode());
        assertEquals(expectedErrorMessage, apiResponse.getBody());
    }


    // --- Tes untuk palingTer ---

    @Test
    @DisplayName("palingTer - Skenario Dasar")
    void palingTer_BasicScenario() {
        final String encodedInput = encodeBase64("10 5 8 10 9 5 10 8 7");
        final String expectedOutput = """
                Tertinggi: 10
                Terendah: 5
                Terbanyak: 10 (3x)
                Tersedikit: 9 (1x)
                Jumlah Tertinggi: 10 * 3 = 30
                Jumlah Terendah: 5 * 2 = 10""";
        final ResponseEntity<String> apiResponse = homeController.palingTer(encodedInput);
        assertEquals(HttpStatus.OK, apiResponse.getStatusCode());
        assertEquals(expectedOutput, apiResponse.getBody());
    }

    @Test
    @DisplayName("palingTer - Input Kosong (Edge Case)")
    void palingTer_EmptyInput() {
        final String encodedInput = encodeBase64("");
        final String expectedOutput = "Tidak ada input";
        final ResponseEntity<String> apiResponse = homeController.palingTer(encodedInput);
        assertEquals(HttpStatus.OK, apiResponse.getStatusCode());
        assertEquals(expectedOutput, apiResponse.getBody());
    }

    @Test
    @DisplayName("palingTer - Tidak Ada Angka Unik (Edge Case)")
    void palingTer_NoUniqueNumber() {
        final String encodedInput = encodeBase64("10 20 10 20");
        final String expectedOutput = "Tidak ada angka unik";
        final ResponseEntity<String> apiResponse = homeController.palingTer(encodedInput);
        assertEquals(HttpStatus.OK, apiResponse.getStatusCode());
        assertEquals(expectedOutput, apiResponse.getBody());
    }

    @Test
    @DisplayName("palingTer - Skenario Tie-Breaker Jumlah Tertinggi (Wins)")
    void palingTer_JumlahTertinggiTieBreak_Wins() {
        final String encodedInput = encodeBase64("10 20 10 9");
        final ResponseEntity<String> apiResponse = homeController.palingTer(encodedInput);
        final String responseBody = apiResponse.getBody();
        assertEquals(HttpStatus.OK, apiResponse.getStatusCode());
        assertTrue(responseBody.contains("Jumlah Tertinggi: 20 * 1 = 20"));
        assertTrue(responseBody.contains("Tersedikit: 9 (1x)"));
    }

    @Test
    @DisplayName("palingTer - Skenario Tie-Breaker Jumlah Tertinggi (Loses)")
    void palingTer_JumlahTertinggiTieBreak_Loses() {
        final String encodedInput = encodeBase64("20 10 10 9");
        final ResponseEntity<String> apiResponse = homeController.palingTer(encodedInput);
        final String responseBody = apiResponse.getBody();

        assertEquals(HttpStatus.OK, apiResponse.getStatusCode());
        assertTrue(responseBody.contains("Jumlah Tertinggi: 20 * 1 = 20"));
        assertTrue(responseBody.contains("Tersedikit: 20 (1x)")); 
    }
    @Test
    @DisplayName("palingTer - Input Teks (Bukan Angka)")
    void palingTer_TextInput() {
        final String encodedInput = encodeBase64("abc");
        final String expectedOutput = "Tidak ada input";
        final ResponseEntity<String> apiResponse = homeController.palingTer(encodedInput);
        assertEquals(HttpStatus.OK, apiResponse.getStatusCode());
        assertEquals(expectedOutput, apiResponse.getBody());
    }

    @Test
    @DisplayName("palingTer - Input Base64 Tidak Valid")
    void palingTer_InvalidBase64() {
        final String malformedBase64 = "!!INVALID!!";
        final String expectedErrorMessage = "Input Base64 tidak valid.";
        final ResponseEntity<String> apiResponse = homeController.palingTer(malformedBase64);
        assertEquals(HttpStatus.BAD_REQUEST, apiResponse.getStatusCode());
        assertTrue(apiResponse.getBody().startsWith(expectedErrorMessage));
    }
}