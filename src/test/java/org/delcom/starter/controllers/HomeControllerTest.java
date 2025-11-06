package org.delcom.starter.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class HomeControllerUnitTest {
    // Test untuk metode hello()
    // --------------------------------
    @Test
    @DisplayName("Mengembalikan pesan selamat datang yang benar")
    void hello_ShouldReturnWelcomeMessage() throws Exception {
        // Arrange
        HomeController controller = new HomeController();

        // Act
        String result = controller.hello();

        // Assert
        assertEquals("Hay, selamat datang di aplikasi dengan Spring Boot!", result);
    }

    // Test untuk metode sayHello()
    // --------------------------------
    @Test
    @DisplayName("Mengembalikan pesan sapaan yang dipersonalisasi")
    void helloWithName_ShouldReturnPersonalizedGreeting() throws Exception {
        // Arrange
        HomeController controller = new HomeController();

        // Act
        String result = controller.sayHello("Abdullah");

        // Assert
        assertEquals("Hello, Abdullah!", result);
    }

    // Test untuk metode informasiNim()
    // --------------------------------
    @Test
    @DisplayName("Menguji semua kemungkinan NIM valid dan tidak valid")
    void informasiNIM_semua_kemungkinan_nim_valid_dan_tidak_valid() throws Exception {
        // Test NIM Tidak Valid
        {
            String input = "11S180";
            String expected = "NIM harus 8 karakter";

            // Arrange
            HomeController controller = new HomeController();

            // Act
            String result = controller.informasiNim(input);

            // Assert
            assertEquals(expected, result);
        }

        // Test NIM Valid dengan Prodi Tidak Tersedia
        {
            String input = "ZZS18005";
            String expected = "Program Studi tidak Tersedia";

            // Arrange
            HomeController controller = new HomeController();

            // Act
            String result = controller.informasiNim(input);

            // Assert
            assertEquals(expected, result);
        }

        // Test Sarjan Informatika
        {
            String input = "11S18005";
            String expected = "Inforamsi NIM 11S18005: >> Program Studi: Sarjana Informatika>> Angkatan: 2018>> Urutan: 5";

            // Arrange
            HomeController controller = new HomeController();

            // Act
            String result = controller.informasiNim(input);

            // Assert
            assertEquals(expected, result);
        }

        // Test Sarjana Sistem Informasi
        {
            String input = "12S18005";
            String expected = "Inforamsi NIM 12S18005: >> Program Studi: Sarjana Sistem Informasi>> Angkatan: 2018>> Urutan: 5";

            // Arrange
            HomeController controller = new HomeController();

            // Act
            String result = controller.informasiNim(input);

            // Assert
            assertEquals(expected, result);
        }

        // Test Sarjana Teknik Elektro
        {
            String input = "14S18005";
            String expected = "Inforamsi NIM 14S18005: >> Program Studi: Sarjana Teknik Elektro>> Angkatan: 2018>> Urutan: 5";

            // Arrange
            HomeController controller = new HomeController();

            // Act
            String result = controller.informasiNim(input);

            // Assert
            assertEquals(expected, result);
        }

        // Test Sarjana Manajemen Rekayasa
        {
            String input = "21S18005";
            String expected = "Inforamsi NIM 21S18005: >> Program Studi: Sarjana Manajemen Rekayasa>> Angkatan: 2018>> Urutan: 5";

            // Arrange
            HomeController controller = new HomeController();

            // Act
            String result = controller.informasiNim(input);

            // Assert
            assertEquals(expected, result);
        }

        // Test Sarjana Teknik Metalurgi
        {
            String input = "22S18005";
            String expected = "Inforamsi NIM 22S18005: >> Program Studi: Sarjana Teknik Metalurgi>> Angkatan: 2018>> Urutan: 5";

            // Arrange
            HomeController controller = new HomeController();

            // Act
            String result = controller.informasiNim(input);

            // Assert
            assertEquals(expected, result);
        }

        // Test Sarjana Teknik Bioproses
        {
            String input = "31S18005";
            String expected = "Inforamsi NIM 31S18005: >> Program Studi: Sarjana Teknik Bioproses>> Angkatan: 2018>> Urutan: 5";

            // Arrange
            HomeController controller = new HomeController();

            // Act
            String result = controller.informasiNim(input);

            // Assert
            assertEquals(expected, result);
        }

        // Test Diploma 4 Teknologi Rekasaya Perangkat Lunak
        {
            String input = "11418005";
            String expected = "Inforamsi NIM 11418005: >> Program Studi: Diploma 4 Teknologi Rekasaya Perangkat Lunak>> Angkatan: 2018>> Urutan: 5";

            // Arrange
            HomeController controller = new HomeController();

            // Act
            String result = controller.informasiNim(input);

            // Assert
            assertEquals(expected, result);
        }

        // Test Diploma 3 Teknologi Informasi
        {
            String input = "11318005";
            String expected = "Inforamsi NIM 11318005: >> Program Studi: Diploma 3 Teknologi Informasi>> Angkatan: 2018>> Urutan: 5";

            // Arrange
            HomeController controller = new HomeController();

            // Act
            String result = controller.informasiNim(input);

            // Assert
            assertEquals(expected, result);
        }

        // Test Diploma 3 Teknologi Komputer
        {
            String input = "13318005";
            String expected = "Inforamsi NIM 13318005: >> Program Studi: Diploma 3 Teknologi Komputer>> Angkatan: 2018>> Urutan: 5";

            // Arrange
            HomeController controller = new HomeController();

            // Act
            String result = controller.informasiNim(input);

            // Assert
            assertEquals(expected, result);
        }
    }

    // Test untuk metode perolehanNilai()
    // --------------------------------
    @Test
    @DisplayName("Perolehan nilai dengan berbagai kombinasi komponen penilaian")
    void perolehanNilai_kombinasi_berbagai_komponen() throws Exception {
        // Perolehan Nilai E
        {
            String inputBase64 = "MA0KMzUNCjENCjE2DQoyMg0KMjYNClR8OTB8MjENClVBU3w5Mnw4Mg0KVUFTfDYzfDE1DQpUfDEwfDUNClVBU3w4OXw3NA0KVHw5NXwzNQ0KUEF8NzV8NDUNClBBfDkwfDc3DQpQQXw4NnwxNA0KVVRTfDIxfDANCkt8NTB8NDQNCi0tLQ0K";
            String expected = """
                    Perolehan Nilai:
                    >> Partisipatif: 54/100 (0.00/0)
                    >> Tugas: 31/100 (10.85/35)
                    >> Kuis: 88/100 (0.88/1)
                    >> Proyek: 0/100 (0.00/16)
                    >> UTS: 0/100 (0.00/22)
                    >> UAS: 70/100 (18.20/26)

                    >> Nilai Akhir: 29.93
                    >> Grade: E
                                        """;
            expected = expected.replaceAll("\n", "<br/>").trim();

            // Arrange
            HomeController controller = new HomeController();

            // Act
            String result = controller.perolehanNilai(inputBase64);

            // Assert
            assertEquals(expected, result);
        }

        // Perolehan Nilai D
        {
            String inputBase64 = "MjcNCjExDQozMg0KMjYNCjENCjMNClBBfDEzfDEzDQpUfDgxfDE4DQpQfDE2fDcNClB8NTN8MjkNCkt8NjV8MjUNClVBU3wzMHwxNQ0KS3w5M3wyDQpVQVN8NjN8MTMNClB8NjJ8NDgNClVUU3w4N3wzMw0KUHw0M3w3DQpVQVN8NDh8MjANCkt8NzB8Ng0KVUFTfDM2fDExDQpLfDY2fDM2DQpQQXw4NHw3MQ0KUHw4OHwyOQ0KUHw0Nnw0Mg0KVUFTfDYwfDM5DQpQQXw1OXw0NQ0KVVRTfDgyfDgNClR8MTJ8NA0KVUFTfDUxfDM5DQpLfDgwfDcwDQpLfDU2fDI0DQpVQVN8MzV8MTMNCkt8MTAwfDMNClR8NTd8Mw0KUEF8ODF8OQ0KVHwyOHwwDQpQQXw4NnwxNg0KS3wzMHwxOA0KVHw3MHwyMQ0KS3w3OHw0DQpQfDQ5fDIzDQpVQVN8MTF8Mg0KUHwzNXwxNw0KUHwyMXwxNg0KUEF8MTF8NA0KVHw1NXwxMQ0KUHw0NnwzNA0KUEF8MzZ8Mw0KUHw4Mnw1OQ0KVHwyOHwyMQ0KS3wxM3wxMQ0KVVRTfDMzfDIyDQpLfDkwfDQNCkt8ODN8NTQNCkt8NTh8MTYNClVUU3w0N3wyNg0KVVRTfDk1fDU4DQpQfDExfDMNClR8NDl8OA0KVVRTfDg2fDENClR8MjN8MjINClBBfDczfDQ5DQpLfDc2fDExDQpUfDE3fDMNClR8MjB8MQ0KUHwzMnwyDQpUfDczfDINClVUU3w3Mnw2NA0KVVRTfDc2fDM5DQpQQXw4NXwxOQ0KVVRTfDk5fDEzDQpQfDU3fDExDQpUfDI0fDEyDQpVQVN8Mjd8MjUNClR8MTB8NQ0KVHw5OHw1NQ0KS3w0OXw4DQpQQXw1OHw1MA0KUEF8ODF8MTUNClBBfDcwfDENClVUU3w4Mnw3Mw0KVHwzOHwzNg0KVUFTfDk1fDM3DQotLS0NCg==";
            String expected = """
                    Perolehan Nilai:
                    >> Partisipatif: 40/100 (10.80/27)
                    >> Tugas: 32/100 (3.52/11)
                    >> Kuis: 28/100 (8.96/32)
                    >> Proyek: 51/100 (13.26/26)
                    >> UTS: 44/100 (0.44/1)
                    >> UAS: 46/100 (1.38/3)

                    >> Nilai Akhir: 38.36
                    >> Grade: D
                    """;
            expected = expected.replaceAll("\n", "<br/>").trim();

            // Arrange
            HomeController controller = new HomeController();

            // Act
            String result = controller.perolehanNilai(inputBase64);

            // Assert
            assertEquals(expected, result);
        }

        // Perolehan Nilai C
        {
            String inputBase64 = "Mw0KMA0KMzMNCjkNCjYNCjQ5DQpVQVN8OTV8ODcNCkt8Mzl8MzINClB8OTB8MjENClVBU3w2OXwxMw0KVUFTfDQ1fDM3DQpVVFN8ODl8ODYNClR8NTR8MzMNClVUU3w0OXwxNA0KUHw5MXw3Nw0KUEF8NDl8MzUNClR8Mjd8Mw0KVVRTfDk4fDEzDQpQQXw0NXw4DQpUfDcwfDcwDQpQfDU2fDE5DQpVQVN8Mjl8MTANCkt8OTF8NzINClBBfDI4fDEzDQpQfDQ0fDI4DQpVQVN8Nzh8NQ0KVUFTfDQ5fDIyDQpVQVN8MjN8Nw0KUHw2NXw0Mg0KUEF8NjN8NDgNClVUU3wzNHwzMw0KVUFTfDExfDkNCkt8ODV8NDQNClBBfDYzfDIwDQpUfDMzfDIyDQpLfDI5fDIzDQpVQVN8MjR8Ng0KVVRTfDQ5fDQ3DQpQQXwxMHw5DQpVVFN8NTl8MTINClVUU3w0N3wyMg0KUHw1Nnw1NQ0KLS0tDQo=";
            String expected = """
                    Perolehan Nilai:
                    >> Partisipatif: 51/100 (1.53/3)
                    >> Tugas: 69/100 (0.00/0)
                    >> Kuis: 70/100 (23.10/33)
                    >> Proyek: 60/100 (5.40/9)
                    >> UTS: 53/100 (3.18/6)
                    >> UAS: 46/100 (22.54/49)

                    >> Nilai Akhir: 55.75
                    >> Grade: C
                    """;
            expected = expected.replaceAll("\n", "<br/>").trim();

            // Arrange
            HomeController controller = new HomeController();

            // Act
            String result = controller.perolehanNilai(inputBase64);

            // Assert
            assertEquals(expected, result);
        }

        // Perolehan Nilai BC
        {
            String inputBase64 = "NTcNCjANCjEwDQoyDQoyOA0KMw0KUEF8NDJ8MzENClBBfDg3fDg1DQpQQXw2OHw2MQ0KS3w2Mnw3DQpUfDExfDEwDQpVVFN8MTAwfDQzDQpUfDY3fDE1DQpVQVN8MTB8MTANCkt8MTF8MTANClBBfDY2fDMyDQpQfDUwfDIwDQpVQVN8NjF8MjkNClVBU3wzOXwzNQ0KVUFTfDM4fDMxDQpQfDY3fDQ4DQpQQXw5OHwzNw0KUHw0MnwzMA0KVVRTfDk1fDU3DQpUfDY1fDM4DQpVQVN8ODJ8MTMNClR8OTV8OTUNClVUU3w4MXwyNg0KUEF8ODN8NTcNClB8Njd8NTQNClB8ODN8NzUNClVUU3wyNnwyMA0KVUFTfDMzfDExDQpVVFN8OTh8OTUNCkt8MTAwfDQzDQpUfDg4fDgzDQpQQXwyMXwyMA0KS3w4OHwxNA0KUHw3N3w3Mw0KS3wxMXwyDQpQfDcyfDQxDQpVVFN8MjV8MTkNClR8NzJ8MzENClB8MTR8Mg0KVVRTfDMxfDYNClVBU3w4Mnw0MQ0KVUFTfDEwfDUNClVUU3w3NHwxNA0KUHw4Mnw2Ng0KUHwzMnwxOA0KS3w4MXwyMQ0KUHw4MXw3NA0KVVRTfDU5fDANClR8ODV8NTcNClB8NjZ8NDENClBBfDE4fDEyDQpQfDI1fDYNClVUU3wyNnw2DQpQfDIxfDIwDQpVVFN8NzZ8NDgNClR8NDd8MzQNClVBU3w1M3wzNA0KVVRTfDU1fDMyDQpQfDk2fDY0DQpVQVN8NTF8MTINClR8NzR8NA0KUEF8Nzd8NDgNCkt8OTR8NTMNClR8MjB8Mg0KVHw3M3wxMA0KVHw2NXwyMQ0KS3wxMnwwDQpVVFN8NzB8MjkNClVBU3w4MXwxMw0KUHw5OXw3Ng0KS3w5OXw2NQ0KS3w1NHw0MA0KVUFTfDEwMHwwDQpVVFN8NjR8MjkNClVBU3wyOHwxMQ0KUEF8NDd8MzMNCkt8NjN8NTkNCi0tLQ0K";
            String expected = """
                    Perolehan Nilai:
                    >> Partisipatif: 68/100 (38.76/57)
                    >> Tugas: 52/100 (0.00/0)
                    >> Kuis: 46/100 (4.60/10)
                    >> Proyek: 72/100 (1.44/2)
                    >> UTS: 48/100 (13.44/28)
                    >> UAS: 36/100 (1.08/3)

                    >> Nilai Akhir: 59.32
                    >> Grade: BC
                    """;
            expected = expected.replaceAll("\n", "<br/>").trim();

            // Arrange
            HomeController controller = new HomeController();

            // Act
            String result = controller.perolehanNilai(inputBase64);

            // Assert
            assertEquals(expected, result);
        }

        // Perolehan Nilai B
        {
            String inputBase64 = "MA0KMA0KMA0KNjYNCjM0DQowDQpLfDkyfDY5DQpVQVN8MTR8MTINClVBU3w1NHwxNg0KVUFTfDUzfDMxDQpQfDYwfDYwDQpUfDc1fDM0DQpVQVN8MzJ8MTcNCkt8Mjh8MTQNClBBfDgwfDQzDQpUfDk3fDg2DQpUfDI2fDIxDQpQQXwyMHwxNQ0KUEF8OTR8NjgNClBBfDM2fDE0DQpLfDExfDYNClVBU3w5MHw2MA0KS3wyMXwxNg0KS3wyM3wxDQpUfDI3fDEyDQpQQXw3NnwxMw0KVUFTfDc3fDM3DQpQQXw0NHwzMA0KVUFTfDg5fDExDQpUfDM0fDEwDQpLfDk1fDQ1DQpUfDk2fDINClBBfDUwfDENClR8MjN8MTQNCkt8ODh8OQ0KUHw5MHw5MA0KS3wxMXw4DQpUfDQ4fDE0DQpQQXwyMHwwDQpUfDc4fDUwDQpLfDMwfDE5DQpLfDE1fDINCkt8NTV8NTUNCkt8MzF8MjUNClVBU3w5MXw4Mw0KS3wxMXw4DQpLfDcwfDI3DQpLfDk3fDQzDQpVQVN8MzJ8Mw0KUEF8OTV8MTANClVBU3w0MXwxOQ0KS3w3M3wzOA0KVHw4OXw1DQpVQVN8Njl8Mg0KUEF8NTJ8MjENCkt8ODJ8MjcNClVBU3w4NHwxDQpQfDEwMHw5OQ0KVUFTfDQ2fDENClVBU3w1M3wyNA0KVHw2MHw0NQ0KVUFTfDEwfDINClR8NTB8MTcNClVBU3w3NHw3NA0KVHw5Mnw3Mg0KVHw1MHwzNQ0KUEF8OTB8NTINCkt8NjR8MjYNCkt8MjN8Nw0KVHw0OXwxMg0KVUFTfDg0fDYyDQpVQVN8ODB8MjUNClBBfDQ2fDQwDQpLfDc5fDUxDQpVQVN8NzZ8MTMNCkt8NTN8MjMNClR8MzB8NQ0KUEF8NTl8MzANClBBfDYzfDYxDQpQQXwxNHw4DQpLfDM2fDI5DQpQQXw3OXwyNg0KVUFTfDU4fDU0DQpLfDI5fDExDQpQQXw4MnwxDQpLfDUyfDQNCkt8NjN8MzANCkt8NDB8MTQNClVBU3wyOHwyNQ0KVUFTfDM4fDExDQpQQXwyMHw5DQpVQVN8Njh8MjcNClR8OTF8MTkNClVBU3w4NXw2Mw0KUEF8NTN8NA0KVUFTfDkzfDg4DQpVQVN8NzZ8NDANCi0tLQ0K";
            String expected = """
                    Perolehan Nilai:
                    >> Partisipatif: 41/100 (0.00/0)
                    >> Tugas: 44/100 (0.00/0)
                    >> Kuis: 47/100 (0.00/0)
                    >> Proyek: 99/100 (65.34/66)
                    >> UTS: 0/100 (0.00/34)
                    >> UAS: 50/100 (0.00/0)

                    >> Nilai Akhir: 65.34
                    >> Grade: B
                    """;
            expected = expected.replaceAll("\n", "<br/>").trim();

            // Arrange
            HomeController controller = new HomeController();

            // Act
            String result = controller.perolehanNilai(inputBase64);

            // Assert
            assertEquals(expected, result);
        }

        // Perolehan Nilai AB
        {
            String inputBase64 = "MA0KMA0KMA0KMA0KMjMNCjc3DQpUfDc0fDUwDQpUfDkwfDYyDQpQQXw2Nnw1NQ0KVHw1M3wyNQ0KVHwxNnw5DQpUfDM3fDIyDQpUfDY4fDM4DQpVQVN8MTAwfDEwMA0KUEF8Mzl8MzgNClR8ODl8NDkNClR8Mjh8MTINClB8Mzh8MA0KS3w2OXwxOA0KVHwxMHwyDQpUfDc1fDMzDQpQQXw1M3wyMA0KS3wxN3wwDQpQQXw1OHw1MA0KUEF8OTF8ODENClR8Mjd8MTMNClB8OTd8MjgNCkt8MTh8MTINClB8MjZ8OA0KUHw5N3wxNQ0KVHw1OHwzOQ0KUHw3N3wxMg0KUHwxMHw2DQpUfDk2fDY2DQpLfDIzfDEzDQpLfDEzfDANClR8NDh8NDcNClBBfDY0fDI1DQpLfDg1fDQxDQpLfDMxfDI3DQpQQXw5NHw0OA0KVHw4Nnw0Ng0KS3w5M3wyMg0KVHw3MXwzMQ0KUHwxN3w3DQpQfDM3fDENClBBfDg0fDc4DQpQQXw1M3wzOA0KS3w2Mnw2MA0KUEF8NDR8MTENClR8NjV8NTYNCkt8MTJ8Mw0KS3wyMnwxNg0KUEF8MzZ8MjENClR8NjZ8MA0KVHw1OXw0Mg0KS3w0M3wyMA0KVHw5Mnw1OQ0KVHw5Mnw4Ng0KUEF8NzJ8MTINClR8MTB8NQ0KUHw3M3wzOA0KUEF8NDZ8MzENClR8MTV8NA0KS3wyMXwxOQ0KUEF8MjF8MTgNClBBfDg0fDU5DQpQQXw3M3wxMg0KS3w1OXwyMA0KS3w0M3w2DQpQfDY5fDQNClR8NDh8MQ0KVHw3MnwyMQ0KS3wyM3wxNQ0KVHw2NXwyOQ0KS3wzNHwxNw0KVHw3MHw2OA0KS3w1OHw0MA0KS3w2M3wzOA0KVHw1OXwyMQ0KUHw2MXwxNw0KS3wzNHwyMw0KUEF8ODV8ODMNCkt8ODV8NjMNClB8OTR8NDANClBBfDkzfDMNCkt8NTd8MjUNClR8MTB8Mg0KS3w2MHwzDQpUfDQ2fDEwDQpUfDg5fDENCkt8MzB8Ng0KVHw5NHwyMA0KLS0tDQo=";
            String expected = """
                    Perolehan Nilai:
                    >> Partisipatif: 59/100 (0.00/0)
                    >> Tugas: 51/100 (0.00/0)
                    >> Kuis: 48/100 (0.00/0)
                    >> Proyek: 25/100 (0.00/0)
                    >> UTS: 0/100 (0.00/23)
                    >> UAS: 100/100 (77.00/77)

                    >> Nilai Akhir: 77.00
                    >> Grade: AB
                    """;
            expected = expected.replaceAll("\n", "<br/>").trim();

            // Arrange
            HomeController controller = new HomeController();

            // Act
            String result = controller.perolehanNilai(inputBase64);

            // Assert
            assertEquals(expected, result);
        }

        // Perolehan Nilai A
        {
            String inputBase64 = "MzYNCjINCjINCjYNCjkNCjQ1DQpLfDg5fDgxDQpVQVN8MTR8MTQNClVBU3wyOXwyOQ0KS3w3Mnw0NQ0KS3w0NHwzNQ0KUEF8NDN8NDMNClVUU3w2N3w0Mg0KVHwzNHwxNQ0KVHw1Nnw0MQ0KUEF8NjF8NjENCkt8MzZ8OQ0KS3w5MXw5MQ0KVHwxOXw4DQpVVFN8MTV8MTUNClR8Mzd8MTINClBBfDkyfDkyDQpUfDgyfDIzDQpQfDU2fDANClVBU3w1MHw1MA0KUEF8ODl8ODgNCkt8NzB8NjINClBBfDYxfDU5DQpVQVN8MzV8MzUNClB8MzJ8MTMNClVBU3w5OXw5OQ0KUHw5N3wxNQ0KUEF8MTR8MTMNClR8OTR8NA0KUHw1MnwzMA0KVVRTfDIxfDEzDQpUfDMwfDI4DQpQQXw3M3w3MA0KUHw5NXwzMw0KVHwzNHwyOQ0KVHw0M3wyMg0KS3wxN3w3DQpQfDQ0fDYNClR8Mjh8MTINClVUU3w3MXwzOA0KUEF8NjR8NjANClVUU3w5NnwzNQ0KUHwxOHwyDQpUfDE4fDE0DQpVVFN8NjR8NTENCkt8MTl8MTANClR8NzB8MjANClR8NzR8MTINClVUU3wzNHw2DQpQfDgwfDY1DQpVVFN8ODV8NTENClB8MzV8MzENClBBfDUwfDMwDQpVQVN8NDN8NDMNClB8Mjd8MTYNCkt8MTl8MTYNClVUU3w5N3wzOQ0KVVRTfDk0fDINClVBU3w0OHw0Ng0KUHw5NnwxMA0KVHwxOXw5DQpVQVN8MTd8Nw0KVHw5NHw2Ng0KUEF8NjB8NTUNClR8MjJ8Mw0KVUFTfDEwMHw5NQ0KUHw0MnwzMw0KUHw3N3wzNQ0KUEF8ODh8ODANClVUU3wyM3wxMw0KS3wxOHw1DQpUfDI1fDYNClB8MTB8MQ0KUHw5NHw5Mw0KVHwzNXwzDQpVVFN8MjB8OA0KS3w3MXwwDQpVQVN8MzJ8MzINClVUU3wxMXw2DQpVQVN8MjN8MjMNClR8OTR8NTANClR8NTR8NTINClVUU3w1MHw0NQ0KVVRTfDMyfDEwDQpVVFN8ODB8Nw0KVHw0OXwyNw0KUHw5M3w0OQ0KVHwzMHwyMA0KVHwyMnwxNg0KS3w1OHw0NQ0KVVRTfDYzfDUzDQpLfDY2fDQxDQotLS0NCg==";
            String expected = """
                    Perolehan Nilai:
                    >> Partisipatif: 93/100 (33.48/36)
                    >> Tugas: 46/100 (0.92/2)
                    >> Kuis: 66/100 (1.32/2)
                    >> Proyek: 45/100 (2.70/6)
                    >> UTS: 47/100 (4.23/9)
                    >> UAS: 96/100 (43.20/45)

                    >> Nilai Akhir: 85.85
                    >> Grade: A
                    """;
            expected = expected.replaceAll("\n", "<br/>").trim();

            // Arrange
            HomeController controller = new HomeController();

            // Act
            String result = controller.perolehanNilai(inputBase64);

            // Assert
            assertEquals(expected, result);
        }

        // Perolehan Nilai dengan data tidak valid: Total komponen tidak 100
        {
            String inputBase64 = "MA0KMzUNCjENCjE2DQoyMg0KMA0K";
            String expected = """
                    Total bobot harus 100
                    """;
            expected = expected.replaceAll("\n", "<br/>").trim();

            // Arrange
            HomeController controller = new HomeController();

            // Act
            String result = controller.perolehanNilai(inputBase64);

            // Assert
            assertEquals(expected, result);
        }

        // Perolehan Nilai dengan berbagai data tidak valid
        {
            String inputBase64 = "MA0KMA0KMA0KNTANCjUwDQowDQpBQkN8MTANClpaWnwxMHwwDQpQfDUwfDUwDQpVVFN8NTB8NTANCi0tLQ0K";
            String expected = """
                    Data tidak valid. Silahkan menggunakan format: Simbol|Bobot|Perolehan-Nilai
                    Simbol tidak dikenal
                    Perolehan Nilai:
                    >> Partisipatif: 0/100 (0.00/0)
                    >> Tugas: 0/100 (0.00/0)
                    >> Kuis: 0/100 (0.00/0)
                    >> Proyek: 100/100 (50.00/50)
                    >> UTS: 100/100 (50.00/50)
                    >> UAS: 0/100 (0.00/0)

                    >> Nilai Akhir: 100.00
                    >> Grade: A
                    """;
            expected = expected.replaceAll("\n", "<br/>").trim();

            // Arrange
            HomeController controller = new HomeController();

            // Act
            String result = controller.perolehanNilai(inputBase64);

            // Assert
            assertEquals(expected, result);
        }
    }

    // Test untuk metode perbedaanL()
    // --------------------------------
    @Test
    @DisplayName("Menghitung perbedaan L pada matriks dengan berbagai ukuran")
    void perbedaanL_matriks_berbagai_ukuran() throws Exception {
        // Test matriks 1x1
        {
            String inputBase64 = "MQ0KMTkNCg==";
            String expected = """
                    Nilai L: Tidak Ada
                    Nilai Kebalikan L: Tidak Ada
                    Nilai Tengah: 19
                    Perbedaan: Tidak Ada
                    Dominan: 19
                    """;
            expected = expected.replaceAll("\n", "<br/>").trim();

            // Arrange
            HomeController controller = new HomeController();

            // Act
            String result = controller.perbedaanL(inputBase64);

            // Assert
            assertEquals(expected, result);
        }

        // Test matriks 2x2
        {
            String inputBase64 = "Mg0KODMgNTUNCjY2IDc1DQo=";
            String expected = """
                    Nilai L: Tidak Ada
                    Nilai Kebalikan L: Tidak Ada
                    Nilai Tengah: 279
                    Perbedaan: Tidak Ada
                    Dominan: 279
                    """;
            expected = expected.replaceAll("\n", "<br/>").trim();

            // Arrange
            HomeController controller = new HomeController();

            // Act
            String result = controller.perbedaanL(inputBase64);

            // Assert
            assertEquals(expected, result);
        }

        // Test matriks 3x3
        {
            String inputBase64 = "Mw0KMSAyIDMNCjQgNSA2DQo3IDggOQ0K";
            String expected = """
                    Nilai L: 20
                    Nilai Kebalikan L: 20
                    Nilai Tengah: 5
                    Perbedaan: 0
                    Dominan: 5
                    """;
            expected = expected.replaceAll("\n", "<br/>").trim();

            // Arrange
            HomeController controller = new HomeController();

            // Act
            String result = controller.perbedaanL(inputBase64);

            // Assert
            assertEquals(expected, result);
        }

        // Test Matrik 16x16
        {
            String inputBase64 = "MTYNCjg2IDIgMTkgNjIgOTkgOTQgODggNzEgNjggMzQgMzQgNTEgNzQgNjkgNDkgMjYNCjg4IDg3IDc4IDk5IDQxIDYyIDI3IDI3IDM3IDQ3IDY2IDc3IDMgMTEgNDIgNTcNCjk0IDUxIDg3IDkgMjUgODAgNjMgNDMgMzkgNDUgNCA1IDg0IDUyIDMgODkNCjczIDI3IDM0IDcyIDkyIDk1IDQgNzggODEgNzkgODggNTAgNjQgOTUgMSAyOA0KOTkgNjIgNDYgMjggMTQgOTggMTIgNjcgMTAgNTEgMzMgMTYgNzYgMjMgMTQgMjENCjY5IDQzIDU4IDM1IDE4IDQzIDgyIDggNDUgOTcgMzAgNDAgMTkgOTkgODUgMzINCjYyIDExIDc1IDgzIDYxIDYyIDc3IDEwIDYwIDQ4IDI1IDY2IDM3IDY1IDQ2IDkyDQozNyAzMCAzMyAyNCA4MyA3NyA5MSA3NSAxMSA0NyAxIDkxIDEwMCA5OSA0NyA5OA0KNjQgNDQgOSAxMDAgOTkgODcgMzYgMSA3NyA1IDE5IDM3IDE2IDg2IDE0IDM3DQo0NSA2OSA2NiAzMyAzOCA3IDIgNTMgMzQgNjUgNDYgODcgNzIgMjQgMTEgNQ0KNDMgNzggMiA1NSA0NiAxMSAzOCA0NiA3OSA2NyAxMCA3OSAyIDI5IDcwIDkzDQo4MSA0NSA4NiA2MCA1MCA5NiAzMSAzNCA3MiA5MSA4MSAzNCA5MSA0MCA0MSA2OQ0KMzQgMTggOTMgOTQgOTYgMTEgOCA3NyAzMCA3MSA0IDQ1IDY4IDY3IDQ1IDg4DQo0MyA0OSA4OSA1NSA4OSA2OCAzNCA3NSA1NiA1IDI4IDI1IDExIDY4IDM0IDEyDQo1NCA0NiA4MCA1NiAzMiA2NiA3OCA2MSA3MCA1NCA2MyA0OCAyNCA3OCA5OCA3Mg0KNDAgMTcgNjUgODUgNjYgMjYgOSAzNCA4MiAxNSA3MyAyMiA3OCA3OCA1OSA0OA0K";

            String expected = """
                    Nilai L: 1721
                    Nilai Kebalikan L: 1681
                    Nilai Tengah: 164
                    Perbedaan: 40
                    Dominan: 1721
                    """;
            expected = expected.replaceAll("\n", "<br/>").trim();

            // Arrange
            HomeController controller = new HomeController();

            // Act
            String result = controller.perbedaanL(inputBase64);

            // Assert
            assertEquals(expected, result);
        }
    }

    // Test untuk metode palingTer()
    // --------------------------------
    @Test
    @DisplayName("Memperolah informasi paling ter dari suatu nilai")
    void palingTer_memperoleh_informasi_paling_ter_dari_suatu_nilai() throws Exception {
        // Pengujian Counter = 0
        {
            String inputBase64 = "LS0tDQo=";
            String expected = "Informasi tidak tersedia";
            expected = expected.trim();

            // Arrange
            HomeController controller = new HomeController();

            // Act
            String result = controller.palingTer(inputBase64);

            // Assert
            assertEquals(expected, result);
        }

        // Pengujian 1
        {
            String inputBase64 = "MQ0KMQ0KMw0KMw0KMg0KMg0KMg0KNA0KNQ0KMQ0KLS0tDQo=";
            String expected = """
                    Tertinggi: 5
                    Terendah: 1
                    Terbanyak: 2 (3x)
                    Tersedikit: 4 (1x)
                    Jumlah Tertinggi: 3 * 2 = 6
                    Jumlah Terendah: 1 * 3 = 3
                    """;
            expected = expected.replaceAll("\n", "<br/>").trim();

            // Arrange
            HomeController controller = new HomeController();

            // Act
            String result = controller.palingTer(inputBase64);

            // Assert
            assertEquals(expected, result);
        }

        // Pengujian 2
        {
            String inputBase64 = "NTgNCjMxDQo4NA0KMTINCjg2DQo2MA0KODINCjQ5DQo4Mw0KNjQNCjc3DQo3DQo2OA0KOTENCjMNCjQxDQo1MQ0KNTMNCjUyDQo3NA0KNzINCjc0DQoxMA0KMzgNCjUwDQo1MQ0KOQ0KNTYNCjU2DQoyNQ0KODQNCjYzDQo5OA0KODQNCjQ4DQo5NQ0KNDYNCjMzDQo3OA0KNDUNCjU1DQoxMA0KNjANCjg4DQoxMA0KNzYNCjgxDQo1NQ0KNDYNCjU4DQozOA0KNTcNCjkyDQo2Nw0KNDENCjQ3DQozMA0KMg0KNjANCjQwDQozNg0KNTMNCjcwDQozMw0KNjYNCjYyDQo1OA0KMTUNCjkyDQo3Mw0KMTcNCjM3DQo2Ng0KMTINCjI5DQoxMDANCjUzDQo2Ng0KMzANCjEyDQozNA0KNg0KMjQNCjg5DQo2NA0KMTENCjgwDQozDQo5MA0KOA0KNjcNCjY1DQoyMg0KNTINCjI2DQozMg0KNjINCjU4DQo0NA0KOTQNCjE4DQo1OA0KMTANCjUNCjE3DQozNg0KMTcNCjYzDQo2OQ0KNzYNCjUyDQo1DQo5NA0KODINCjgNCjg1DQo4NQ0KNDcNCjQzDQozOA0KNzcNCjQzDQo0MQ0KNzMNCjgxDQozNg0KMjYNCjE3DQo0NQ0KOQ0KNDANCjczDQo1OQ0KMw0KNTgNCjYzDQoyNg0KMjkNCjE3DQo2OA0KOTQNCjM5DQo1DQo1MA0KMjQNCjQ0DQozNg0KMjQNCjUNCjkyDQo4NQ0KNTQNCjI2DQo5Mg0KNTINCjI1DQoyMw0KNDkNCjQ5DQo0Nw0KMjYNCjQ1DQo2DQo0OA0KNTkNCjk5DQozMA0KMjENCjEyDQoyMA0KNjENCjU2DQo4OA0KNDENCjQ4DQo1Nw0KNzQNCjY0DQozNA0KNjENCjEwDQo2DQo0OQ0KNjYNCjM1DQo1DQoyNg0KODUNCjc1DQo0OQ0KODkNCjY4DQoxMQ0KMTcNCjUNCjg0DQoxMQ0KNjINCjkzDQoyNw0KODkNCjg2DQo0NA0KNA0KOTMNCjI4DQo2Nw0KNDINCjY2DQoyDQo3Mg0KMTENCjEwDQo0NQ0KNTANCjMyDQoxOA0KMzANCjk1DQoyNQ0KMjINCjI2DQoxMDANCjI3DQo3Mw0KNDcNCjUNCjIwDQo4OA0KODkNCjcNCjk3DQoyOA0KMjUNCjQ0DQo3Nw0KMTENCjQ2DQo5Ng0KOTMNCjY3DQo1MQ0KNDkNCjY1DQo3OQ0KODkNCjg5DQotLS0NCg==";
            String expected = """
                    Tertinggi: 100
                    Terendah: 2
                    Terbanyak: 26 (7x)
                    Tersedikit: 35 (1x)
                    Jumlah Tertinggi: 89 * 6 = 534
                    Jumlah Terendah: 2 * 2 = 4
                    """;
            expected = expected.replaceAll("\n", "<br/>").trim();

            // Arrange
            HomeController controller = new HomeController();

            // Act
            String result = controller.palingTer(inputBase64);

            // Assert
            assertEquals(expected, result);
        }
    }
    
}
