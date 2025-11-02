package org.delcom.starter.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
// pastikan Locale di-import jika belum
import java.util.Locale; 

import static org.junit.jupiter.api.Assertions.*;

class HomeControllerTest {

    private HomeController homeController; // Variabel diubah

    @BeforeEach
    void setUp() {
        homeController = new HomeController(); // Variabel diubah
    }

    private String encodeBase64(String text) {
        return Base64.getEncoder().encodeToString(text.getBytes(StandardCharsets.UTF_8));
    }

    // --- Tes Asli ---

    @Test
    @DisplayName("Mengembalikan pesan selamat datang yang benar")
    void hello_ShouldReturnWelcomeMessage() {
        String responseBody = homeController.hello(); // Variabel diubah
        assertEquals("Hay Abdullah, selamat datang di pengembangan aplikasi dengan Spring Boot!", responseBody);
    }

    @Test
    @DisplayName("Mengembalikan pesan sapaan yang dipersonalisasi")
    void helloWithName_ShouldReturnPersonalizedGreeting() {
        String responseBody = homeController.sayHello("Abdullah"); // Variabel diubah
        assertEquals("Hello, Abdullah!", responseBody);
    }

    // --- Tes untuk informasiNim ---

    @Test
    @DisplayName("informasiNim - NIM Valid (11S)")
    void informasiNim_Valid() {
        String testNim = "11S24001"; // Variabel diubah
        String expectedOutput = """
                Inforamsi NIM 11S24001:\s
                >> Program Studi: Sarjana Informatika
                >> Angkatan: 2024
                >> Urutan: 1"""; // Variabel diubah

        ResponseEntity<String> apiResponse = homeController.informasiNim(testNim); // Variabel diubah

        assertEquals(HttpStatus.OK, apiResponse.getStatusCode());
        assertEquals(expectedOutput, apiResponse.getBody());
    }

    @Test
    @DisplayName("informasiNim - NIM Panjang Tidak Valid")
    void informasiNim_InvalidLength() {
        String testNim = "11S24"; // Variabel diubah
        String expectedOutput = "Format NIM tidak valid. Harap masukkan 8 digit."; // Variabel diubah
        ResponseEntity<String> apiResponse = homeController.informasiNim(testNim); // Variabel diubah
        assertEquals(HttpStatus.BAD_REQUEST, apiResponse.getStatusCode());
        assertEquals(expectedOutput, apiResponse.getBody());
    }

    @Test
    @DisplayName("informasiNim - Prefix NIM Tidak Dikenal")
    void informasiNim_InvalidPrefix() {
        String testNim = "99S24001"; // Variabel diubah
        String expectedOutput = "Prefix NIM '99S' tidak ditemukan."; // Variabel diubah
        ResponseEntity<String> apiResponse = homeController.informasiNim(testNim); // Variabel diubah
        assertEquals(HttpStatus.BAD_REQUEST, apiResponse.getStatusCode());
        assertEquals(expectedOutput, apiResponse.getBody());
    }

    @Test
    @DisplayName("informasiNim - Input Parse Error (Memicu Catch)")
    void informasiNim_InvalidParse() {
        String testNim = "11SXX001"; // Variabel diubah
        ResponseEntity<String> apiResponse = homeController.informasiNim(testNim); // Variabel diubah
        assertEquals(HttpStatus.BAD_REQUEST, apiResponse.getStatusCode());
        assertTrue(apiResponse.getBody().contains("For input string: \"XX\""));
    }

    // --- Tes untuk perolehanNilai ---

    @Test
    @DisplayName("perolehanNilai - Skenario Kalkulasi Lengkap (Grade A)")
    void perolehanNilai_FullScenario() {
        String testInputData = String.join("\n", // Variabel diubah
                "10 15 10 15 20 30",
                "PA|100|80", "T|100|90", "K|100|85", "P|100|95", "UTS|100|75", "UAS|100|88", "---"
        );
        String encodedInput = encodeBase64(testInputData); // Variabel diubah

        String expectedOutput = """
                Perolehan Nilai:
                >> Partisipatif: 80/100 (8.00/10)
                >> Tugas: 90/100 (13.50/15)
                >> Kuis: 85/100 (8.50/10)
                >> Proyek: 95/100 (14.25/15)
                >> UTS: 75/100 (15.00/20)
                >> UAS: 88/100 (26.40/30)

                >> Nilai Akhir: 85.65
                >> Grade: A"""; // Variabel diubah

        ResponseEntity<String> apiResponse = homeController.perolehanNilai(encodedInput); // Variabel diubah
        assertEquals(HttpStatus.OK, apiResponse.getStatusCode());
        assertEquals(expectedOutput, apiResponse.getBody());
    }

    @Test
    @DisplayName("perolehanNilai - Skenario Grade AB")
    void perolehanNilai_GradeAB() {
        String testInputData = String.join("\n", "10 15 10 15 20 30", "PA|100|75", "T|100|75", "K|100|75", "P|100|75", "UTS|100|75", "UAS|100|75", "---"); // Variabel diubah
        String encodedInput = encodeBase64(testInputData); // Variabel diubah
        ResponseEntity<String> apiResponse = homeController.perolehanNilai(encodedInput); // Variabel diubah
        String responseBody = apiResponse.getBody(); // Variabel diubah
        assertEquals(HttpStatus.OK, apiResponse.getStatusCode());
        assertTrue(responseBody.contains(">> Nilai Akhir: 75.00"));
        assertTrue(responseBody.contains(">> Grade: AB"));
    }

    @Test
    @DisplayName("perolehanNilai - Skenario Grade B")
    void perolehanNilai_GradeB() {
        String testInputData = String.join("\n", "10 15 10 15 20 30", "PA|100|65", "T|100|65", "K|100|65", "P|100|65", "UTS|100|65", "UAS|100|65", "---"); // Variabel diubah
        String encodedInput = encodeBase64(testInputData); // Variabel diubah
        ResponseEntity<String> apiResponse = homeController.perolehanNilai(encodedInput); // Variabel diubah
        String responseBody = apiResponse.getBody(); // Variabel diubah
        assertEquals(HttpStatus.OK, apiResponse.getStatusCode());
        assertTrue(responseBody.contains(">> Nilai Akhir: 65.00"));
        assertTrue(responseBody.contains(">> Grade: B"));
    }
    
    @Test
    @DisplayName("perolehanNilai - Skenario Grade BC")
    void perolehanNilai_GradeBC() {
        String testInputData = String.join("\n", "10 15 10 15 20 30", "PA|100|60", "T|100|60", "K|100|60", "P|100|60", "UTS|100|60", "UAS|100|60", "---"); // Variabel diubah
        String encodedInput = encodeBase64(testInputData); // Variabel diubah
        ResponseEntity<String> apiResponse = homeController.perolehanNilai(encodedInput); // Variabel diubah
        String responseBody = apiResponse.getBody(); // Variabel diubah
        assertEquals(HttpStatus.OK, apiResponse.getStatusCode());
        assertTrue(responseBody.contains(">> Nilai Akhir: 60.00"));
        assertTrue(responseBody.contains(">> Grade: BC"));
    }

    @Test
    @DisplayName("perolehanNilai - Skenario Grade C")
    void perolehanNilai_GradeC() {
        String testInputData = String.join("\n", "10 15 10 15 20 30", "PA|100|50", "T|100|50", "K|100|50", "P|100|50", "UTS|100|50", "UAS|100|50", "---"); // Variabel diubah
        String encodedInput = encodeBase64(testInputData); // Variabel diubah
        ResponseEntity<String> apiResponse = homeController.perolehanNilai(encodedInput); // Variabel diubah
        String responseBody = apiResponse.getBody(); // Variabel diubah
        assertEquals(HttpStatus.OK, apiResponse.getStatusCode());
        assertTrue(responseBody.contains(">> Nilai Akhir: 50.00"));
        assertTrue(responseBody.contains(">> Grade: C"));
    }

    @Test
    @DisplayName("perolehanNilai - Skenario Grade D")
    void perolehanNilai_GradeD() {
        String testInputData = String.join("\n", "10 15 10 15 20 30", "PA|100|40", "T|100|40", "K|100|40", "P|100|40", "UTS|100|40", "UAS|100|40", "---"); // Variabel diubah
        String encodedInput = encodeBase64(testInputData); // Variabel diubah
        ResponseEntity<String> apiResponse = homeController.perolehanNilai(encodedInput); // Variabel diubah
        String responseBody = apiResponse.getBody(); // Variabel diubah
        assertEquals(HttpStatus.OK, apiResponse.getStatusCode());
        assertTrue(responseBody.contains(">> Nilai Akhir: 40.00"));
        assertTrue(responseBody.contains(">> Grade: D"));
    }

    @Test
    @DisplayName("perolehanNilai - Skenario Input Jarang (Sparse)")
    void perolehanNilai_SparseInput() {
        String testInputData = String.join("\n", "10 15 10 15 20 30", "T|100|90", "UTS|100|50", "---"); // Variabel diubah
        String encodedInput = encodeBase64(testInputData); // Variabel diubah
        ResponseEntity<String> apiResponse = homeController.perolehanNilai(encodedInput); // Variabel diubah
        String responseBody = apiResponse.getBody(); // Variabel diubah
        assertEquals(HttpStatus.OK, apiResponse.getStatusCode());
        assertTrue(responseBody.contains(">> Partisipatif: 0/100 (0.00/10)"));
        assertTrue(responseBody.contains(">> Kuis: 0/100 (0.00/10)"));
        assertTrue(responseBody.contains(">> Nilai Akhir: 23.50"));
        assertTrue(responseBody.contains(">> Grade: E"));
    }

    @Test
    @DisplayName("perolehanNilai - Skenario Simbol Tidak Valid")
    void perolehanNilai_InvalidSymbol() {
        String testInputData = String.join("\n", "10 15 10 15 20 30", "PA|100|80", "XYZ|100|100", "---"); // Variabel diubah
        String encodedInput = encodeBase64(testInputData); // Variabel diubah
        ResponseEntity<String> apiResponse = homeController.perolehanNilai(encodedInput); // Variabel diubah
        String responseBody = apiResponse.getBody(); // Variabel diubah
        assertEquals(HttpStatus.OK, apiResponse.getStatusCode());
        assertTrue(responseBody.contains(">> Nilai Akhir: 8.00"));
    }

    // --- PERBAIKAN UNTUK COVERAGE (Garis kuning di `while`) ---
    @Test
    @DisplayName("perolehanNilai - Skenario Input Data Kosong (Hanya Bobot)")
    void perolehanNilai_InputDataKosong() {
        // Tes ini menguji 'while' loop dilewati (evaluasi 'false' di awal)
        // Input "...\n" (dengan newline) adalah valid dan akan skip loop
        String testInputData = "10 15 10 15 20 30\n"; // Variabel diubah
        String encodedInput = encodeBase64(testInputData); // Variabel diubah
        
        ResponseEntity<String> apiResponse = homeController.perolehanNilai(encodedInput); // Variabel diubah
        String responseBody = apiResponse.getBody(); // Variabel diubah

        // Tes ini SEHARUSNYA 200 OK
        assertEquals(HttpStatus.OK, apiResponse.getStatusCode());
        assertTrue(responseBody.contains(">> Nilai Akhir: 0.00"));
        assertTrue(responseBody.contains(">> Grade: E"));
    }

    @Test
    @DisplayName("perolehanNilai - Input Malformed (Memicu Catch Block)")
    void perolehanNilai_InvalidInput() {
        String encodedInput = encodeBase64("halo"); // Variabel diubah
        String expectedErrorMessage = "Format data input tidak valid atau tidak lengkap. Pastikan angka dan format sudah benar."; // Variabel diubah
        ResponseEntity<String> apiResponse = homeController.perolehanNilai(encodedInput); // Variabel diubah
        assertEquals(HttpStatus.BAD_REQUEST, apiResponse.getStatusCode());
        assertEquals(expectedErrorMessage, apiResponse.getBody());
    }

    @Test
    @DisplayName("perolehanNilai - Input Base64 Tidak Valid")
    void perolehanNilai_InvalidBase64() {
        String malformedBase64 = "!!INVALID!!"; // Variabel diubah
        String expectedErrorMessage = "Input Base64 tidak valid."; // Variabel diubah
        ResponseEntity<String> apiResponse = homeController.perolehanNilai(malformedBase64); // Variabel diubah
        assertEquals(HttpStatus.BAD_REQUEST, apiResponse.getStatusCode());
        assertTrue(apiResponse.getBody().startsWith(expectedErrorMessage));
    }


    // --- Tes untuk perbedaanL ---

    @Test
    @DisplayName("perbedaanL - Matriks 3x3 (Ganjil, Dominan=Tengah)")
    void perbedaanL_Matrix3x3() {
        String testInputData = String.join("\n", "3", "1 2 3", "4 5 6", "7 8 9"); // Variabel diubah
        String encodedInput = encodeBase64(testInputData); // Variabel diubah
        String expectedOutput = """
                Nilai L: 20:
                Nilai Kebalikan L: 20
                Nilai Tengah: 5
                Perbedaan: 0
                Dominan: 5"""; // Variabel diubah
        ResponseEntity<String> apiResponse = homeController.perbedaanL(encodedInput); // Variabel diubah
        assertEquals(HttpStatus.OK, apiResponse.getStatusCode());
        assertEquals(expectedOutput, apiResponse.getBody());
    }
    
    @Test
    @DisplayName("perbedaanL - Matriks 4x4 (Genap, Dominan=L)")
    void perbedaanL_Matrix4x4() {
        String testInputData = String.join("\n", "4", "1 2 3 4", "5 6 7 8", "9 10 11 12", "13 14 15 16"); // Variabel diubah
        String encodedInput = encodeBase64(testInputData); // Variabel diubah
        String expectedOutput = """
                Nilai L: 57:
                Nilai Kebalikan L: 45
                Nilai Tengah: 34
                Perbedaan: 12
                Dominan: 57"""; // Variabel diubah
        ResponseEntity<String> apiResponse = homeController.perbedaanL(encodedInput); // Variabel diubah
        assertEquals(HttpStatus.OK, apiResponse.getStatusCode());
        assertEquals(expectedOutput, apiResponse.getBody());
    }

    @Test
    @DisplayName("perbedaanL - Matriks 1x1 (Edge Case)")
    void perbedaanL_Matrix1x1() {
        String encodedInput = encodeBase64("1\n42"); // Variabel diubah
        String expectedOutput = """
                Nilai L: Tidak Ada
                Nilai Kebalikan L: Tidak Ada
                Nilai Tengah: 42
                Perbedaan: Tidak Ada
                Dominan: 42"""; // Variabel diubah
        ResponseEntity<String> apiResponse = homeController.perbedaanL(encodedInput); // Variabel diubah
        assertEquals(HttpStatus.OK, apiResponse.getStatusCode());
        assertEquals(expectedOutput, apiResponse.getBody());
    }

    @Test
    @DisplayName("perbedaanL - Matriks 2x2 (Edge Case)")
    void perbedaanL_Matrix2x2() {
        String encodedInput = encodeBase64("2\n1 2\n3 4"); // Variabel diubah
        String expectedOutput = """
                Nilai L: Tidak Ada
                Nilai Kebalikan L: Tidak Ada
                Nilai Tengah: 10
                Perbedaan: Tidak Ada
                Dominan: 10"""; // Variabel diubah
        ResponseEntity<String> apiResponse = homeController.perbedaanL(encodedInput); // Variabel diubah
        assertEquals(HttpStatus.OK, apiResponse.getStatusCode());
        assertEquals(expectedOutput, apiResponse.getBody());
    }

    @Test
    @DisplayName("perbedaanL - Input Data Malformed (Memicu Catch)")
    void perbedaanL_InvalidInputData() {
        String encodedInput = encodeBase64("abc"); // Variabel diubah
        String expectedErrorMessage = "Format data matriks tidak valid atau tidak lengkap."; // Variabel diubah
        ResponseEntity<String> apiResponse = homeController.perbedaanL(encodedInput); // Variabel diubah
        assertEquals(HttpStatus.BAD_REQUEST, apiResponse.getStatusCode());
        assertEquals(expectedErrorMessage, apiResponse.getBody());
    }

    @Test
    @DisplayName("perbedaanL - Input Base64 Tidak Valid")
    void perbedaanL_InvalidBase64() {
        String malformedBase64 = "!!INVALID!!"; // Variabel diubah
        String expectedErrorMessage = "Input Base64 tidak valid."; // Variabel diubah
        ResponseEntity<String> apiResponse = homeController.perbedaanL(malformedBase64); // Variabel diubah
        
        assertEquals(HttpStatus.BAD_REQUEST, apiResponse.getStatusCode());
        assertTrue(apiResponse.getBody().startsWith(expectedErrorMessage));
    }

    // --- Tes untuk palingTer ---

    @Test
    @DisplayName("palingTer - Skenario Dasar")
    void palingTer_BasicScenario() {
        String encodedInput = encodeBase64("10 5 8 10 9 5 10 8 7"); // Variabel diubah
        String expectedOutput = """
                Tertinggi: 10
                Terendah: 5
                Terbanyak: 10 (3x)
                Tersedikit: 9 (1x)
                Jumlah Tertinggi: 10 * 3 = 30
                Jumlah Terendah: 5 * 2 = 10"""; // Variabel diubah
        ResponseEntity<String> apiResponse = homeController.palingTer(encodedInput); // Variabel diubah
        assertEquals(HttpStatus.OK, apiResponse.getStatusCode());
        assertEquals(expectedOutput, apiResponse.getBody());
    }

    @Test
    @DisplayName("palingTer - Input Kosong (Edge Case)")
    void palingTer_EmptyInput() {
        String encodedInput = encodeBase64(""); // Variabel diubah
        String expectedOutput = "Tidak ada input"; // Variabel diubah
        ResponseEntity<String> apiResponse = homeController.palingTer(encodedInput); // Variabel diubah
        assertEquals(HttpStatus.OK, apiResponse.getStatusCode());
        assertEquals(expectedOutput, apiResponse.getBody());
    }

    @Test
    @DisplayName("palingTer - Tidak Ada Angka Unik (Edge Case)")
    void palingTer_NoUniqueNumber() {
        String encodedInput = encodeBase64("10 20 10 20"); // Variabel diubah
        String expectedOutput = "Tidak ada angka unik"; // Variabel diubah
        ResponseEntity<String> apiResponse = homeController.palingTer(encodedInput); // Variabel diubah
        assertEquals(HttpStatus.OK, apiResponse.getStatusCode());
        assertEquals(expectedOutput, apiResponse.getBody());
    }

    @Test
    @DisplayName("palingTer - Skenario Tie-Breaker Jumlah Tertinggi (Wins)")
    void palingTer_JumlahTertinggiTieBreak_Wins() {
        // Tes ini menguji (prod == jtProd && v > jtVal) -> TRUE
        String encodedInput = encodeBase64("10 20 10 9"); // Variabel diubah
        ResponseEntity<String> apiResponse = homeController.palingTer(encodedInput); // Variabel diubah
        String responseBody = apiResponse.getBody(); // Variabel diubah
        assertEquals(HttpStatus.OK, apiResponse.getStatusCode());
        assertTrue(responseBody.contains("Jumlah Tertinggi: 20 * 1 = 20")); // 20 menang
        assertTrue(responseBody.contains("Tersedikit: 9 (1x)"));
    }

   // --- TES BARU UNTUK COVERAGE (Garis kuning tie-break) ---
    @Test
    @DisplayName("palingTer - Skenario Tie-Breaker Jumlah Tertinggi (Loses)")
    void palingTer_JumlahTertinggiTieBreak_Loses() {
        String encodedInput = encodeBase64("20 10 10 9"); // Variabel diubah
        ResponseEntity<String> apiResponse = homeController.palingTer(encodedInput); // Variabel diubah
        String responseBody = apiResponse.getBody(); // Variabel diubah

        assertEquals(HttpStatus.OK, apiResponse.getStatusCode());
        assertTrue(responseBody.contains("Jumlah Tertinggi: 20 * 1 = 20"));

        assertTrue(responseBody.contains("Tersedikit: 20 (1x)")); 
    }
    @Test
    @DisplayName("palingTer - Input Teks (Bukan Angka)")
    void palingTer_TextInput() {
        String encodedInput = encodeBase64("abc"); // Variabel diubah
        String expectedOutput = "Tidak ada input"; // Variabel diubah
        ResponseEntity<String> apiResponse = homeController.palingTer(encodedInput); // Variabel diubah
        assertEquals(HttpStatus.OK, apiResponse.getStatusCode());
        assertEquals(expectedOutput, apiResponse.getBody());
    }

    @Test
    @DisplayName("palingTer - Input Base64 Tidak Valid")
    void palingTer_InvalidBase64() {
        String malformedBase64 = "!!INVALID!!"; // Variabel diubah
        String expectedErrorMessage = "Input Base64 tidak valid."; // Variabel diubah
        ResponseEntity<String> apiResponse = homeController.palingTer(malformedBase64); // Variabel diubah
        assertEquals(HttpStatus.BAD_REQUEST, apiResponse.getStatusCode());
        assertTrue(apiResponse.getBody().startsWith(expectedErrorMessage));
    }
}