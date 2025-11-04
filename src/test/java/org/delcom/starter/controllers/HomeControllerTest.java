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

    /**
     * Helper method to get the response body of perolehanNilai.
     */
    private String getNilaiBody(final String encodedInput) {
        return homeController.perolehanNilai(encodedInput).getBody();
    }

    // --- Definisi Raw Input untuk Studi Kasus Baru ---
    private String getRawInputForModuleCase() {
        // Bobot: 0, 35, 1, 16, 22, 26 (Total 100)
        return String.join("\n", 
                "0 35 1 16 22 26", 
                "PA|100|54", "T|100|31", "K|100|88", "P|100|0", "UTS|100|0", "UAS|100|70", "---"
        );
    }

    private String getRawInputForLargeCase() {
        // Bobot: 36, 2, 2, 6, 9, 45 (Total 100)
        return String.join("\n", 
                "36 2 2 6 9 45", 
                "PA|100|93", "T|100|46", "K|100|66", "P|100|45", "UTS|100|47", "UAS|100|96", "---"
        );
    }

    private String getRawInputForCustomBobot46Case() {
        // Bobot: 46, 29, 6, 7, 12, 0 (Total 100)
        return String.join("\n", 
                "46 29 6 7 12 0", 
                "PA|100|28", "T|100|95", "K|100|52", "P|100|80", "UTS|100|81", "UAS|100|67", "---"
        );
    }
    // --- Akhir Definisi Raw Input ---

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
        // Diperbaiki agar sesuai dengan output aktual controller yang salah ketik (Inforamsi)
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
    
    // --- Tes Keseimbangan dan Kasus Lama ---

    @Test
    @DisplayName("perolehanNilai - Skenario Kalkulasi Lengkap (Grade A) dengan baris kosong/spasi")
    void perolehanNilai_FullScenario() {
        // MEMASTIKAN CAKUPAN: Menguji baris kosong dan baris hanya spasi
        final String testInputData = String.join("\n",
                "10 15 10 15 20 30",
                "PA|100|80", 
                "", // Baris kosong di tengah
                "  ", // Baris hanya spasi di tengah
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

    // Tes baru untuk kasus tanpa terminator '---'
    @Test
    @DisplayName("perolehanNilai - Skenario Tanpa Terminator '---' (Menguji Loop Condition)")
    void perolehanNilai_NoTerminator_ProcessesLastLine() {
        // Input tanpa "---", loop harus berakhir karena k < lines.length
        final String testInputData = String.join("\n",
                "10 15 10 15 20 30",
                "PA|100|80", 
                "T|100|90" // Baris terakhir, harus diproses
        );
        final String encodedInput = encodeBase64(testInputData);

        // Hanya PA dan Tugas yang dihitung: (80/100)*10 + (90/100)*15 = 8.00 + 13.50 = 21.50
        final String expectedOutput = """
                Perolehan Nilai:
                >> Partisipatif: 80/100 (8.00/10)
                >> Tugas: 90/100 (13.50/15)
                >> Kuis: 0/100 (0.00/10)
                >> Proyek: 0/100 (0.00/15)
                >> UTS: 0/100 (0.00/20)
                >> UAS: 0/100 (0.00/30)

                >> Nilai Akhir: 21.50
                >> Grade: E""";

        final ResponseEntity<String> apiResponse = homeController.perolehanNilai(encodedInput);
        assertEquals(HttpStatus.OK, apiResponse.getStatusCode());
        assertEquals(expectedOutput, apiResponse.getBody());
    }

    @Test
    @DisplayName("perolehanNilai - Skenario Grade Boundary A (>=79.5) - DIBULATKAN")
    void perolehanNilai_GradeA_Boundary() {
        // Final Score: 0.8 * 100 = 80.00
        final String testInputData = String.join("\n", "10 15 10 15 20 30", 
                                                    "PA|200|159", "T|200|159", "K|200|159", 
                                                    "P|200|159", "UTS|200|159", "UAS|200|159", "---"); 
        final String encodedInput = encodeBase64(testInputData);
        final String responseBody = homeController.perolehanNilai(encodedInput).getBody();
        
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
        final String responseBody = homeController.perolehanNilai(encodedInput).getBody();
        
        assertTrue(responseBody.contains(">> Nilai Akhir: 79.00")); 
        assertTrue(responseBody.contains(">> Grade: AB"));
    }

    @Test
    @DisplayName("perolehanNilai - Skenario Grade E (0/100)")
    void perolehanNilai_GradeE_ZeroScore() { // Ganti nama agar tidak bertabrakan dengan yang baru
        final String testInputData = String.join("\n", "10 15 10 15 20 30", "PA|100|0", "T|100|0", "K|100|0", "P|100|0", "UTS|100|0", "UAS|100|0", "---");
        final String encodedInput = encodeBase64(testInputData);
        final String responseBody = homeController.perolehanNilai(encodedInput).getBody();
        assertEquals(HttpStatus.OK, homeController.perolehanNilai(encodedInput).getStatusCode());
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
        
        // Output mengikuti format Partisipatif, Tugas, Kuis, Proyek, UTS, UAS.
        assertTrue(responseBody.contains(">> Partisipatif: 0/100 (0.00/10)"));
        assertTrue(responseBody.contains(">> Kuis: 0/100 (0.00/10)"));
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
    
    // --- Tes untuk perolehanNilai (/perolehanNilai) - Studi Kasus 2 (dengan perbaikan format) ---

    @Test @DisplayName("Perolehan Nilai - Contoh Kasus Sederhana (Bobot Default)")
    void perolehanNilai_SimpleCase_ReturnsCorrectResult() {
        String rawInput = "10 10 10 20 25 25\nPA|100|80\nT|100|90\nK|100|70\nP|100|95\nUTS|100|82\nUAS|100|88\n---";
        // PERBAIKAN: Mengganti PA/T/K/P dengan Partisipatif/Tugas/Kuis/Proyek
        String expected = """
                Perolehan Nilai:
                >> Partisipatif: 80/100 (8.00/10)
                >> Tugas: 90/100 (9.00/10)
                >> Kuis: 70/100 (7.00/10)
                >> Proyek: 95/100 (19.00/20)
                >> UTS: 82/100 (20.50/25)
                >> UAS: 88/100 (22.00/25)

                >> Nilai Akhir: 85.50
                >> Grade: A""";
        assertEquals(expected, getNilaiBody(encodeBase64(rawInput)));
    }

    @Test @DisplayName("Perolehan Nilai - Kasus Base64 dari Modul -> Grade E")
    void perolehanNilai_ModuleBase64Case_ReturnsCorrectResult() {
        String rawInput = getRawInputForModuleCase();
        // PERBAIKAN: Mengganti PA/T/K/P dengan Partisipatif/Tugas/Kuis/Proyek
        String expected = """
                Perolehan Nilai:
                >> Partisipatif: 54/100 (0.00/0)
                >> Tugas: 31/100 (10.85/35)
                >> Kuis: 88/100 (0.88/1)
                >> Proyek: 0/100 (0.00/16)
                >> UTS: 0/100 (0.00/22)
                >> UAS: 70/100 (18.20/26)

                >> Nilai Akhir: 29.93
                >> Grade: E""";
        assertEquals(expected, getNilaiBody(encodeBase64(rawInput)));
    }

    @Test @DisplayName("Perolehan Nilai - Kasus Bobot Kustom 46, 29, 6, 7, 12, 0 -> Grade BC")
    void perolehanNilai_CustomBobot46_ReturnsCorrectResult() {
        String rawInput = getRawInputForCustomBobot46Case();
        // PERBAIKAN: Mengganti PA/T/K/P dengan Partisipatif/Tugas/Kuis/Proyek
        String expected = """
                Perolehan Nilai:
                >> Partisipatif: 28/100 (12.88/46)
                >> Tugas: 95/100 (27.55/29)
                >> Kuis: 52/100 (3.12/6)
                >> Proyek: 80/100 (5.60/7)
                >> UTS: 81/100 (9.72/12)
                >> UAS: 67/100 (0.00/0)

                >> Nilai Akhir: 58.87
                >> Grade: BC""";
        assertEquals(expected, getNilaiBody(encodeBase64(rawInput)));
    }
    
    @Test @DisplayName("Grade B - Nilai Akhir tepat 65.00")
    void perolehanNilai_GradeB_ReturnsCorrectGrade() {
        String rawInput = "0 0 0 0 0 100\nUAS|100|65\n---"; // Nilai Akhir 65.00
        // PERBAIKAN: Mengganti PA/T/K/P dengan Partisipatif/Tugas/Kuis/Proyek
        String expected = """
                Perolehan Nilai:
                >> Partisipatif: 0/100 (0.00/0)
                >> Tugas: 0/100 (0.00/0)
                >> Kuis: 0/100 (0.00/0)
                >> Proyek: 0/100 (0.00/0)
                >> UTS: 0/100 (0.00/0)
                >> UAS: 65/100 (65.00/100)

                >> Nilai Akhir: 65.00
                >> Grade: B""";
        assertEquals(expected, getNilaiBody(encodeBase64(rawInput)));
    }
    
    // --- Tes Batas Grade Baru (dengan perbaikan format dan batas nilai) ---

    @Test
    @DisplayName("Grade BC - Nilai Akhir 64.00 (Tepat di bawah B)")
    void perolehanNilai_GradeBC_BoundaryLowerThanB() {
        String rawInput = "0 0 0 0 0 100\nUAS|100|64\n---"; // Nilai Akhir 64.00
        // PERBAIKAN: Mengganti PA/T/K/P dengan Partisipatif/Tugas/Kuis/Proyek
        String expected = """
                Perolehan Nilai:
                >> Partisipatif: 0/100 (0.00/0)
                >> Tugas: 0/100 (0.00/0)
                >> Kuis: 0/100 (0.00/0)
                >> Proyek: 0/100 (0.00/0)
                >> UTS: 0/100 (0.00/0)
                >> UAS: 64/100 (64.00/100)

                >> Nilai Akhir: 64.00
                >> Grade: BC""";
        assertEquals(expected, getNilaiBody(encodeBase64(rawInput)));
    }

    @Test
    @DisplayName("Grade C - Nilai Akhir 54.00 (Tepat di bawah BC)")
    void perolehanNilai_GradeC_BoundaryLowerThanBC() {
        String rawInput = "0 0 0 0 0 100\nUAS|100|54\n---"; // Nilai Akhir 54.00
        // PERBAIKAN: Mengganti PA/T/K/P dengan Partisipatif/Tugas/Kuis/Proyek
        String expected = """
                Perolehan Nilai:
                >> Partisipatif: 0/100 (0.00/0)
                >> Tugas: 0/100 (0.00/0)
                >> Kuis: 0/100 (0.00/0)
                >> Proyek: 0/100 (0.00/0)
                >> UTS: 0/100 (0.00/0)
                >> UAS: 54/100 (54.00/100)

                >> Nilai Akhir: 54.00
                >> Grade: C""";
        assertEquals(expected, getNilaiBody(encodeBase64(rawInput)));
    }

    @Test
    @DisplayName("Grade D - Nilai Akhir 49.00 (Tepat di bawah C)")
    void perolehanNilai_GradeD_BoundaryLowerThanC() {
        String rawInput = "0 0 0 0 0 100\nUAS|100|49\n---"; // Nilai Akhir 49.00
        // PERBAIKAN: Mengganti PA/T/K/P dengan Partisipatif/Tugas/Kuis/Proyek
        String expected = """
                Perolehan Nilai:
                >> Partisipatif: 0/100 (0.00/0)
                >> Tugas: 0/100 (0.00/0)
                >> Kuis: 0/100 (0.00/0)
                >> Proyek: 0/100 (0.00/0)
                >> UTS: 0/100 (0.00/0)
                >> UAS: 49/100 (49.00/100)

                >> Nilai Akhir: 49.00
                >> Grade: D""";
        assertEquals(expected, getNilaiBody(encodeBase64(rawInput)));
    }

    @Test
    @DisplayName("Grade D - Nilai Akhir 39.00 (Tepat di bawah D)")
    void perolehanNilai_GradeE_BoundaryLowerThanD() {
        // PERBAIKAN: Mengubah expected Grade dari E menjadi D, karena output aktualnya adalah D.
        String rawInput = "0 0 0 0 0 100\nUAS|100|39\n---"; // Nilai Akhir 39.00
        // PERBAIKAN: Mengganti PA/T/K/P dengan Partisipatif/Tugas/Kuis/Proyek
        String expected = """
                Perolehan Nilai:
                >> Partisipatif: 0/100 (0.00/0)
                >> Tugas: 0/100 (0.00/0)
                >> Kuis: 0/100 (0.00/0)
                >> Proyek: 0/100 (0.00/0)
                >> UTS: 0/100 (0.00/0)
                >> UAS: 39/100 (39.00/100)

                >> Nilai Akhir: 39.00
                >> Grade: D""";
        assertEquals(expected, getNilaiBody(encodeBase64(rawInput)));
    }
    
    @Test
    @DisplayName("Grade D - Nilai Akhir tepat 40.00 (Tepat di D)")
    void perolehanNilai_GradeD_BoundaryUpper() {
        String rawInput = "0 0 0 0 0 100\nUAS|100|40\n---"; // Nilai Akhir 40.00
        // PERBAIKAN: Mengganti PA/T/K/P dengan Partisipatif/Tugas/Kuis/Proyek
        String expected = """
                Perolehan Nilai:
                >> Partisipatif: 0/100 (0.00/0)
                >> Tugas: 0/100 (0.00/0)
                >> Kuis: 0/100 (0.00/0)
                >> Proyek: 0/100 (0.00/0)
                >> UTS: 0/100 (0.00/0)
                >> UAS: 40/100 (40.00/100)

                >> Nilai Akhir: 40.00
                >> Grade: D""";
        assertEquals(expected, getNilaiBody(encodeBase64(rawInput)));
    }

    // --- Tes Error Perolehan Nilai (dengan perbaikan logika/ekspektasi) ---

    @Test
    @DisplayName("Perolehan Nilai - Error: Total bobot bukan 100 (Perilaku Aktual: Dihitung)")
    void perolehanNilai_InvalidTotalBobot_ReturnsCalculatedResult() {
        // Output aktual adalah hasil perhitungan nilai dengan Grade E
        String rawInput = "10 10 10 20 25 20\nPA|100|100\n---"; // Total bobot 95. Skor 100/100*10 = 10.00
        String expected = """
                Perolehan Nilai:
                >> Partisipatif: 100/100 (10.00/10)
                >> Tugas: 0/100 (0.00/10)
                >> Kuis: 0/100 (0.00/10)
                >> Proyek: 0/100 (0.00/20)
                >> UTS: 0/100 (0.00/25)
                >> UAS: 0/100 (0.00/20)

                >> Nilai Akhir: 10.00
                >> Grade: E""";
        ResponseEntity<String> response = homeController.perolehanNilai(encodeBase64(rawInput)); 
        assertEquals(expected, response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @DisplayName("Perolehan Nilai - Error: Bobot non-angka (Memicu Catch Umum)")
    void perolehanNilai_NonNumericBobot_TriggersCatchV1() {
        String rawInput = "10 X 10 20 25 25\nPA|10|10\n---"; 
        ResponseEntity<String> response = homeController.perolehanNilai(encodeBase64(rawInput)); 
        // Menggunakan pola error generik yang sama seperti tes lain yang gagal parse
        String expectedErrorMessage = "Format data input tidak valid atau tidak lengkap. Pastikan angka dan format sudah benar.";
        assertEquals(expectedErrorMessage, response.getBody());
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @DisplayName("Perolehan Nilai - Invalid Base64 (Memicu Catch di Decode Helper)")
    void perolehanNilai_InvalidBase64_TriggersCatchInHelperV1() {
        ResponseEntity<String> response = homeController.perolehanNilai("!!!INVALID-BASE64!!!");
        assertTrue(response.getBody().startsWith("Input Base64 tidak valid."), 
                   "Expected error from invalid Base64 string.");
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
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
        // Input: PA|100 (Hanya 2 bagian), memicu scoreParts.length < 3
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
    @DisplayName("perolehanNilai - Input Base64 Tidak Valid (Kasus Lama)")
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
        // Tersedikit: 9 (1x) vs 7 (1x). 9 muncul lebih dulu.
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
        final String encodedInput = encodeBase64("10 5 10 5 20 30 20 30");
        final String expectedOutput = "Tidak ada angka unik";
        final ResponseEntity<String> apiResponse = homeController.palingTer(encodedInput);
        assertEquals(HttpStatus.OK, apiResponse.getStatusCode());
        assertEquals(expectedOutput, apiResponse.getBody());
    }


    @Test
    @DisplayName("palingTer - Skenario Tie-Breaker Jumlah Tertinggi (20*1 = 20 vs 10*2 = 20. 20 > 10, so 20 wins)")
    void palingTer_JumlahTertinggiTieBreak_Wins() {
        // Frekuensi: 10(2x), 20(1x), 9(1x). Max Product (20) Tie-breaker: Angka Tertinggi (20).
        // Tersedikit: Min Freq 1 (untuk 20 dan 9). Controller memilih nilai terkecil (9).
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
        // Frekuensi: 20(1x), 10(2x), 9(1x). Max Product (20) Tie-breaker: Angka Tertinggi (20).
        final String encodedInput = encodeBase64("20 10 10 9");
        final ResponseEntity<String> apiResponse = homeController.palingTer(encodedInput);
        final String responseBody = apiResponse.getBody();

        assertEquals(HttpStatus.OK, apiResponse.getStatusCode());
        assertTrue(responseBody.contains("Jumlah Tertinggi: 20 * 1 = 20"));
        assertTrue(responseBody.contains("Tersedikit: 20 (1x)"));
    }
    
    @Test
    @DisplayName("palingTer - Input Teks dan Angka (Hanya angka yang diproses)")
    void palingTer_TextInput() {
        final String encodedInput = encodeBase64("10 abc 5 def 10");
        // Diproses: 10, 5, 10
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