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

    /**
     * Helper method to encode a string to Base64.
     */
    private String encodeBase64(final String text) {
        return Base64.getEncoder().encodeToString(text.getBytes(StandardCharsets.UTF_8));
    }

    // --- Tes Bawaan ---

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

    // --- Tes untuk informasiNim (/informasiNim/{nim}) ---

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
    @DisplayName("informasiNim - Input Parse Error (Angkatan bukan angka)")
    void informasiNim_InvalidParse_Cohort() {
        final String testNim = "11SXX001";
        final ResponseEntity<String> apiResponse = homeController.informasiNim(testNim);
        assertEquals(HttpStatus.BAD_REQUEST, apiResponse.getStatusCode());
        assertTrue(apiResponse.getBody().contains("For input string: \"XX\""));
    }
    
    @Test
    @DisplayName("informasiNim - Input Parse Error (Urutan bukan angka)")
    void informasiNim_InvalidParse_Sequence() {
        final String testNim = "11S24XYZ";
        final ResponseEntity<String> apiResponse = homeController.informasiNim(testNim);
        assertEquals(HttpStatus.BAD_REQUEST, apiResponse.getStatusCode());
        assertTrue(apiResponse.getBody().contains("For input string: \"XYZ\""));
    }

    // --- Tes untuk perolehanNilai (/perolehanNilai) ---

    @Test
    @DisplayName("perolehanNilai - Skenario Kalkulasi Lengkap (Grade A) dengan baris kosong")
    void perolehanNilai_FullScenario() {
        final String testInputData = String.join("\n",
                "10 15 10 15 20 30",
                "PA|100|80", 
                "", // Baris kosong di tengah
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
    @DisplayName("perolehanNilai - Skenario Grade E (0/100)")
    void perolehanNilai_GradeE() {
        final String testInputData = String.join("\n", "10 15 10 15 20 30", "PA|100|0", "T|100|0", "K|100|0", "P|100|0", "UTS|100|0", "UAS|100|0", "---");
        final String encodedInput = encodeBase64(testInputData);
        final ResponseEntity<String> apiResponse = homeController.perolehanNilai(encodedInput);
        final String responseBody = apiResponse.getBody();
        assertEquals(HttpStatus.OK, apiResponse.getStatusCode());
        assertTrue(responseBody.contains(">> Nilai Akhir: 0.00"));
        assertTrue(responseBody.contains(">> Grade: E"));
    }

    @Test
    @DisplayName("perolehanNilai - Skenario Input Jarang (Sparse) - Nol jika maxScore=0")
    void perolehanNilai_SparseInput_ZeroCase() {
        // Hanya Tugas dan UTS yang ada nilainya
        final String testInputData = String.join("\n", "10 15 10 15 20 30", "T|100|90", "UTS|100|50", "---");
        final String encodedInput = encodeBase64(testInputData);
        final ResponseEntity<String> apiResponse = homeController.perolehanNilai(encodedInput);
        final String responseBody = apiResponse.getBody();
        assertEquals(HttpStatus.OK, apiResponse.getStatusCode());
        
        // PA, Kuis, Proyek, UAS tidak ada, sehingga average=0/100, weightedScore=0.00/Weight
        assertTrue(responseBody.contains(">> Partisipatif: 0/100 (0.00/10)"));
        assertTrue(responseBody.contains(">> Kuis: 0/100 (0.00/10)"));
        
        // Tugas: 90/100 (13.50/15) + UTS: 50/100 (10.00/20) = 23.50
        assertTrue(responseBody.contains(">> Tugas: 90/100 (13.50/15)"));
        assertTrue(responseBody.contains(">> UTS: 50/100 (10.00/20)"));
        
        assertTrue(responseBody.contains(">> Nilai Akhir: 23.50"));
        assertTrue(responseBody.contains(">> Grade: E"));
    }

    @Test
    @DisplayName("perolehanNilai - Skenario Simbol Tidak Valid - Diabaikan")
    void perolehanNilai_InvalidSymbol() {
        // Hanya PA yang valid
        final String testInputData = String.join("\n", "10 15 10 15 20 30", "PA|100|80", "XYZ|100|100", "---");
        final String encodedInput = encodeBase64(testInputData);
        final ResponseEntity<String> apiResponse = homeController.perolehanNilai(encodedInput);
        final String responseBody = apiResponse.getBody();
        assertEquals(HttpStatus.OK, apiResponse.getStatusCode());
        // Nilai akhir hanya dari PA: 80/100 * 10 = 8.00
        assertTrue(responseBody.contains(">> Nilai Akhir: 8.00"));
    }

    @Test
    @DisplayName("perolehanNilai - Input Data Kosong (Hanya Bobot)")
    void perolehanNilai_InputDataOnlyWeights() {
        final String testInputData = "10 15 10 15 20 30\n---"; // Hanya bobot dan terminator
        final String encodedInput = encodeBase64(testInputData);
        
        final ResponseEntity<String> apiResponse = homeController.perolehanNilai(encodedInput);
        final String responseBody = apiResponse.getBody();

        assertEquals(HttpStatus.OK, apiResponse.getStatusCode());
        // Semua rata-rata 0/100 * bobot = 0.00
        assertTrue(responseBody.contains(">> Nilai Akhir: 0.00"));
        assertTrue(responseBody.contains(">> Grade: E"));
    }
    
    @Test
    @DisplayName("perolehanNilai - Input Kosong Total (Memicu Exception: Data bobot tidak ditemukan)")
    void perolehanNilai_EmptyInput_TriggersException() {
        final String encodedInput = encodeBase64(""); 
        final String expectedErrorMessage = "Format data input tidak valid atau tidak lengkap. Pastikan angka dan format sudah benar.";
        final ResponseEntity<String> apiResponse = homeController.perolehanNilai(encodedInput);
        assertEquals(HttpStatus.BAD_REQUEST, apiResponse.getStatusCode());
        assertEquals(expectedErrorMessage, apiResponse.getBody());
    }

    @Test
    @DisplayName("perolehanNilai - Input Bobot Tidak Lengkap (Memicu Exception)")
    void perolehanNilai_IncompleteWeights_TriggersException() {
        final String encodedInput = encodeBase64("10 15 10 15 20"); // Hanya 5 bobot
        final String expectedErrorMessage = "Format data input tidak valid atau tidak lengkap. Pastikan angka dan format sudah benar.";
        final ResponseEntity<String> apiResponse = homeController.perolehanNilai(encodedInput);
        assertEquals(HttpStatus.BAD_REQUEST, apiResponse.getStatusCode());
        assertEquals(expectedErrorMessage, apiResponse.getBody());
    }

    @Test
    @DisplayName("perolehanNilai - Input Nilai Detail Tidak Lengkap (Memicu Exception)")
    void perolehanNilai_IncompleteDetailScore_TriggersException() {
        final String testInputData = String.join("\n", 
                                "10 15 10 15 20 30", 
                                "PA|100", // Baris ini memicu NumberFormatException karena tidak ada 3 part
                                "---");
        final String encodedInput = encodeBase64(testInputData);
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


    // --- Tes untuk perbedaanL (/perbedaanL) ---

    @Test
    @DisplayName("perbedaanL - Matriks 3x3 (Ganjil, Dominan=Tengah)")
    void perbedaanL_Matrix3x3() {
        // L: 1+4+7+8 = 20. Kebalikan L: 3+6+9+2 = 20. Tengah: 5. Perbedaan: 0. Dominan: 5.
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
        // L: 1+5+9+13 + 14+15 = 57. Kebalikan L: 4+8+12+16 + 2+3 = 45. Tengah: 6+7+10+11 = 34. Perbedaan: 12. Dominan: 57.
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
        // Tengah: 1+2+3+4 = 10
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
    @DisplayName("perbedaanL - Input Data Malformed (N bukan angka)")
    void perbedaanL_InvalidInputData_N_NotNumber() {
        final String encodedInput = encodeBase64("abc");
        final String expectedErrorMessage = "Format data matriks tidak valid atau tidak lengkap.";
        final ResponseEntity<String> apiResponse = homeController.perbedaanL(encodedInput);
        assertEquals(HttpStatus.BAD_REQUEST, apiResponse.getStatusCode());
        assertEquals(expectedErrorMessage, apiResponse.getBody());
    }

    @Test
    @DisplayName("perbedaanL - Input Data Malformed (Nilai bukan angka)")
    void perbedaanL_InvalidInputData_Value_NotNumber() {
        final String encodedInput = encodeBase64("3 1 2 X 4 5 6 7 8 9");
        final String expectedErrorMessage = "Format data matriks tidak valid atau tidak lengkap.";
        final ResponseEntity<String> apiResponse = homeController.perbedaanL(encodedInput);
        assertEquals(HttpStatus.BAD_REQUEST, apiResponse.getStatusCode());
        assertEquals(expectedErrorMessage, apiResponse.getBody());
    }

    @Test
    @DisplayName("perbedaanL - Input Data Kosong Total (Memicu Exception)")
    void perbedaanL_EmptyInput_TriggersException() {
        final String encodedInput = encodeBase64(""); 
        final String expectedErrorMessage = "Format data matriks tidak valid atau tidak lengkap.";
        final ResponseEntity<String> apiResponse = homeController.perbedaanL(encodedInput);
        assertEquals(HttpStatus.BAD_REQUEST, apiResponse.getStatusCode());
        assertEquals(expectedErrorMessage, apiResponse.getBody());
    }
    
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

    @Test
    @DisplayName("perbedaanL - Input Base64 Tidak Valid")
    void perbedaanL_InvalidBase64() {
        final String malformedBase64 = "!!INVALID!!";
        final String expectedErrorMessage = "Input Base64 tidak valid.";
        final ResponseEntity<String> apiResponse = homeController.perbedaanL(malformedBase64);
        
        assertEquals(HttpStatus.BAD_REQUEST, apiResponse.getStatusCode());
        assertTrue(apiResponse.getBody().startsWith(expectedErrorMessage));
    }


    // --- Tes untuk palingTer (/palingTer) ---

    @Test
    @DisplayName("palingTer - Skenario Dasar")
    void palingTer_BasicScenario() {
        // Tertinggi: 10, Terendah: 5
        // Frekuensi: 10(3x), 5(2x), 8(2x), 9(1x), 7(1x). Terbanyak: 10.
        // Urutan: 10, 5, 8, 10, 9, 5, 10, 8, 7.
        // Iterasi: 
        // i=0 (10). j=3 (10). Removed: {10, 5, 8}. i=4.
        // i=4 (9). j=9. leastFrequentUnique=9. Stop.
        // Jumlah Tertinggi: 10*3=30. Jumlah Terendah: 5*2=10.
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
    @DisplayName("palingTer - Tidak Ada Angka Unik (Edge Case: Semua berpasangan/berulang)")
    void palingTer_NoUniqueNumber() {
        // Urutan: 10, 20, 10, 20. Semua terhapus.
        final String encodedInput = encodeBase64("10 20 10 20");
        final String expectedOutput = "Tidak ada angka unik";
        final ResponseEntity<String> apiResponse = homeController.palingTer(encodedInput);
        assertEquals(HttpStatus.OK, apiResponse.getStatusCode());
        assertEquals(expectedOutput, apiResponse.getBody());
    }
    
    @Test
    @DisplayName("palingTer - Tidak Ada Angka Unik (Edge Case: Semua terhapus)")
    void palingTer_NoUniqueNumber_Complex() {
        // Urutan: 10, 5, 10, 5, 20, 30, 20, 30.
        // i=0(10). j=2(10). Removed: {10, 5}. i=3.
        // i=3(5). j=9 (tidak ditemukan). Current number list size is 8.
        // *Re-run the logic carefully*:
        // List: [10, 5, 10, 5, 20, 30, 20, 30]
        // i=0 (10). j=2 (10). Removed: {10, 5}. i=3.
        // i=3 (5). Removed. Skip. i=4.
        // i=4 (20). j=6 (20). Removed: {10, 5, 20, 30}. i=7.
        // i=7 (30). Removed. Skip. i=8.
        // leastFrequentUnique = -1.
        final String encodedInput = encodeBase64("10 5 10 5 20 30 20 30");
        final String expectedOutput = "Tidak ada angka unik";
        final ResponseEntity<String> apiResponse = homeController.palingTer(encodedInput);
        assertEquals(HttpStatus.OK, apiResponse.getStatusCode());
        assertEquals(expectedOutput, apiResponse.getBody());
    }


    @Test
    @DisplayName("palingTer - Skenario Tie-Breaker Jumlah Tertinggi (20*1 = 20 vs 10*2 = 20. 20 > 10, so 20 wins)")
    void palingTer_JumlahTertinggiTieBreak_Wins() {
        // Tertinggi: 20. Terendah: 9. Terbanyak: 10 (2x). Tersedikit: 9 (1x).
        // Frekuensi: 10(2x), 20(1x), 9(1x).
        // Product: 10*2=20, 20*1=20, 9*1=9.
        // Tie between 10 (product 20) and 20 (product 20). 20 is larger, so 20 wins.
        final String encodedInput = encodeBase64("10 20 10 9");
        final ResponseEntity<String> apiResponse = homeController.palingTer(encodedInput);
        final String responseBody = apiResponse.getBody();
        assertEquals(HttpStatus.OK, apiResponse.getStatusCode());
        assertTrue(responseBody.contains("Jumlah Tertinggi: 20 * 1 = 20"));
        assertTrue(responseBody.contains("Tersedikit: 9 (1x)"));
    }

    @Test
    @DisplayName("palingTer - Skenario Tie-Breaker Jumlah Tertinggi (20*1 = 20 vs 10*2 = 20. 20 wins)")
    void palingTer_JumlahTertinggiTieBreak_Loses() {
        // Frekuensi: 20(1x), 10(2x), 9(1x).
        // Product: 20*1=20, 10*2=20, 9*1=9.
        // Tie between 20 (product 20) and 10 (product 20). 20 is larger, so 20 wins.
        // Tersedikit: 20
        final String encodedInput = encodeBase64("20 10 10 9");
        final ResponseEntity<String> apiResponse = homeController.palingTer(encodedInput);
        final String responseBody = apiResponse.getBody();

        assertEquals(HttpStatus.OK, apiResponse.getStatusCode());
        assertTrue(responseBody.contains("Jumlah Tertinggi: 20 * 1 = 20"));
        // 20, 10, 10, 9 -> i=0(20). j=9. leastFrequentUnique=20.
        assertTrue(responseBody.contains("Tersedikit: 20 (1x)")); 
    }
    
    @Test
    @DisplayName("palingTer - Input Teks dan Angka (Hanya angka yang diproses)")
    void palingTer_TextInput() {
        final String encodedInput = encodeBase64("10 abc 5 def 10");
        // Diproses: 10, 5, 10
        // Tertinggi: 10, Terendah: 5. Terbanyak: 10 (2x).
        // Urutan: 10, 5, 10. i=0(10). j=2(10). Removed: {10, 5}. i=3. leastFrequentUnique = -1.
        final String expectedOutput = "Tidak ada angka unik";
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