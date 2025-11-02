package org.delcom.starter.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException; // Import spesifik
import java.util.Scanner;
import java.util.Set;

/**
 * Controller utama untuk aplikasi Spring Boot.
 * Meng-handle rute-rute dasar dan adaptasi dari studi kasus.
 *
 * <p>Refaktor ini mematuhi batasan satu file dengan memisahkan
 * logika endpoint (Web Layer) dari logika bisnis (Service Layer)
 * menggunakan metode private di dalam kelas yang sama.</p>
 */
@RestController
public class HomeController {

    // --- Konstanta untuk Standar Penilaian ---
    private static final double GRADE_A_THRESHOLD = 79.5;
    private static final double GRADE_AB_THRESHOLD = 72.0;
    private static final double GRADE_B_THRESHOLD = 64.5;
    private static final double GRADE_BC_THRESHOLD = 57.0;
    private static final double GRADE_C_THRESHOLD = 49.5;
    private static final double GRADE_D_THRESHOLD = 34.0;

    /**
     * Map konstan untuk data program studi.
     * Didefinisikan sebagai static final agar hanya dibuat sekali.
     */
    private static final Map<String, String> PROGRAM_STUDI_MAP = Map.ofEntries(
            Map.entry("11S", "Sarjana Informatika"),
            Map.entry("12S", "Sarjana Sistem Informasi"),
            Map.entry("14S", "Sarjana Teknik Elektro"),
            Map.entry("21S", "Sarjana Manajemen Rekayasa"),
            Map.entry("22S", "Sarjana Teknik Metalurgi"),
            Map.entry("31S", "Sarjana Teknik Bioproses"),
            Map.entry("114", "Diploma 4 Teknologi Rekasaya Perangkat Lunak"),
            Map.entry("113", "Diploma 3 Teknologi Informasi"),
            Map.entry("133", "Diploma 3 Teknologi Komputer")
    );


    // --- Endpoint Bawaan (Web Layer) ---

    @GetMapping("/") /*  */
    public String hello() {
        return "Hay Abdullah, selamat datang di pengembangan aplikasi dengan Spring Boot!";
    }

    @GetMapping("/hello/{name}")
    public String sayHello(@PathVariable String name) {
        return "Hello, " + name + "!";
    }

    // --- Endpoint Studi Kasus (Web Layer) ---

    /**
     * Endpoint untuk adaptasi StudiKasus1 (Informasi NIM).
     * Menerima NIM dan mendelegasikannya ke metode processNimInfo.
     * Mengembalikan 200 OK jika berhasil, 400 Bad Request jika format NIM salah.
     */
    @GetMapping("/informasiNim/{nim}")
    public ResponseEntity<String> informasiNim(@PathVariable String nim) {
        try {
            String result = processNimInfo(nim);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            // Tangkap semua error validasi (panjang, prefix, atau format angka)
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Endpoint untuk adaptasi StudiKasus2 (Perolehan Nilai).
     * Menerima input nilai (Base64) dan mendelegasikannya ke metode processNilai.
     * Mengembalikan 200 OK jika berhasil, 400 Bad Request jika input tidak valid.
     */
    @GetMapping("/perolehanNilai")
    public ResponseEntity<String> perolehanNilai(@RequestParam String strBase64) {
        try {
            String decodedInput = decodeBase64(strBase64);
            String result = processNilai(decodedInput);
            return ResponseEntity.ok(result);
        } catch (NoSuchElementException | ArrayIndexOutOfBoundsException | NumberFormatException e) {
            // Blok catch spesifik (termasuk NumberFormatException) didahulukan
            return new ResponseEntity<>("Format data input tidak valid atau tidak lengkap. Pastikan angka dan format sudah benar.", HttpStatus.BAD_REQUEST);
        } catch (IllegalArgumentException e) {
            // Blok catch umum (induk dari NumberFormatException)
            return new ResponseEntity<>("Input Base64 tidak valid.", HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Endpoint untuk adaptasi StudiKasus3 (Perbedaan L Matriks).
     * Menerima input matriks (Base64) dan mendelegasikannya ke metode processMatrix.
     * Mengembalikan 200 OK jika berhasil, 400 Bad Request jika input tidak valid.
     */
    @GetMapping("/perbedaanL")
    public ResponseEntity<String> perbedaanL(@RequestParam String strBase64) {
        try {
            String decodedInput = decodeBase64(strBase64);
            String result = processMatrix(decodedInput);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>("Input Base64 tidak valid.", HttpStatus.BAD_REQUEST);
        } catch (NoSuchElementException e) {
            // Ini akan ter-trigger jika input "abc"
            return new ResponseEntity<>("Format data matriks tidak valid atau tidak lengkap.", HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Endpoint untuk adaptasi StudiKasus4 (Analisis Frekuensi).
     * Menerima input list angka (Base64) dan mendelegasikannya ke metode processPalingTer.
     * Mengembalikan 200 OK jika berhasil, 400 Bad Request jika input tidak valid.
     */
    @GetMapping("/palingTer")
    public ResponseEntity<String> palingTer(@RequestParam String strBase64) {
        try {
            String decodedInput = decodeBase64(strBase64);
            String result = processPalingTer(decodedInput);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>("Input Base64 tidak valid.", HttpStatus.BAD_REQUEST);
        }
        // Blok catch (NoSuchElementException e) dihapus karena unreachable.
        // processPalingTer menggunakan hasNextInt() yang mencegah exception ini.
    }


    // --- Helper & Utility Methods (Private) ---

    /**
     * Helper method untuk men-decode string Base64.
     * Menghindari duplikasi kode di tiga endpoint.
     *
     * @param strBase64 String input yang di-encode Base64.
     * @return String yang sudah di-decode.
     * @throws IllegalArgumentException jika input Base64 tidak valid.
     */
    private String decodeBase64(String strBase64) {
        try {
            byte[] decodedBytes = Base64.getDecoder().decode(strBase64);
            return new String(decodedBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            // Melempar exception yang lebih spesifik untuk ditangkap oleh endpoint
            throw new IllegalArgumentException("Input Base64 tidak valid: " + e.getMessage());
        }
    }

    /**
     * Helper method untuk mengonversi skor numerik menjadi nilai huruf (Grade).
     *
     * @param score Nilai akhir (double).
     * @return String yang merepresentasikan Grade (A, AB, B, ... E).
     */
    private String getGrade(double score) {
        if (score >= GRADE_A_THRESHOLD) return "A";
        else if (score >= GRADE_AB_THRESHOLD) return "AB";
        else if (score >= GRADE_B_THRESHOLD) return "B";
        else if (score >= GRADE_BC_THRESHOLD) return "BC";
        else if (score >= GRADE_C_THRESHOLD) return "C";
        else if (score >= GRADE_D_THRESHOLD) return "D";
        else return "E";
    }

    // --- Business Logic Methods (Private "Service Layer") ---

    /**
     * Logika inti untuk Studi Kasus 1: Informasi NIM.
     *
     * @param nim NIM yang akan diproses.
     * @return String hasil format informasi.
     * @throws IllegalArgumentException jika format NIM tidak valid.
     */
    private String processNimInfo(String nim) {
        StringBuilder sb = new StringBuilder();

        if (nim.length() != 8) {
            throw new IllegalArgumentException("Format NIM tidak valid. Harap masukkan 8 digit.");
        }

        String prefix = nim.substring(0, 3);
        String angkatanStr = nim.substring(3, 5);
        String nomorUrut = nim.substring(5);
        String namaProgramStudi = PROGRAM_STUDI_MAP.get(prefix);

        if (namaProgramStudi != null) {
            // NumberFormatException (subclass dari IllegalArgumentException) akan ditangkap oleh endpoint
            int tahunAngkatan = 2000 + Integer.parseInt(angkatanStr);
            sb.append("Inforamsi NIM ").append(nim).append(": \n");
            sb.append(">> Program Studi: ").append(namaProgramStudi).append("\n");
            sb.append(">> Angkatan: ").append(tahunAngkatan).append("\n");
            sb.append(">> Urutan: ").append(Integer.parseInt(nomorUrut));
        } else {
            throw new IllegalArgumentException("Prefix NIM '" + prefix + "' tidak ditemukan.");
        }
        return sb.toString();
    }

    /**
     * Logika inti untuk Studi Kasus 2: Perolehan Nilai.
     *
     * @param input String dekode yang berisi data nilai.
     * @return String hasil format perolehan nilai.
     */
    private String processNilai(String input) {
        StringBuilder sb = new StringBuilder();
        try (Scanner scanner = new Scanner(input)) {
            scanner.useLocale(Locale.US);

            int paWeight = scanner.nextInt();
            int assignmentWeight = scanner.nextInt();
            int quizWeight = scanner.nextInt();
            int projectWeight = scanner.nextInt();
            int midExamWeight = scanner.nextInt();
            int finalExamWeight = scanner.nextInt();
            scanner.nextLine();

            int totalPA = 0, maxPA = 0;
            int totalAssignment = 0, maxAssignment = 0;
            int totalQuiz = 0, maxQuiz = 0;
            int totalProject = 0, maxProject = 0;
            int totalMidExam = 0, maxMidExam = 0;
            int totalFinalExam = 0, maxFinalExam = 0;

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.equals("---")) break;

                String[] parts = line.split("\\|");
                String symbol = parts[0];
                int maxScore = Integer.parseInt(parts[1]);
                int score = Integer.parseInt(parts[2]);

                switch (symbol) {
                    case "PA": maxPA += maxScore; totalPA += score; break;
                    case "T": maxAssignment += maxScore; totalAssignment += score; break;
                    case "K": maxQuiz += maxScore; totalQuiz += score; break;
                    case "P": maxProject += maxScore; totalProject += score; break;
                    case "UTS": maxMidExam += maxScore; totalMidExam += score; break;
                    case "UAS": maxFinalExam += maxScore; totalFinalExam += score; break;
                    default: break;
                }
            }

            double avgPA = (maxPA == 0) ? 0 : (totalPA * 100.0 / maxPA);
            double avgAssignment = (maxAssignment == 0) ? 0 : (totalAssignment * 100.0 / maxAssignment);
            double avgQuiz = (maxQuiz == 0) ? 0 : (totalQuiz * 100.0 / maxQuiz);
            double avgProject = (maxProject == 0) ? 0 : (totalProject * 100.0 / maxProject);
            double avgMidExam = (maxMidExam == 0) ? 0 : (totalMidExam * 100.0 / maxMidExam);
            double avgFinalExam = (maxFinalExam == 0) ? 0 : (totalFinalExam * 100.0 / maxFinalExam);

            int roundedPA = (int) Math.round(avgPA);
            int roundedAssignment = (int) Math.round(avgAssignment);
            int roundedQuiz = (int) Math.round(avgQuiz);
            int roundedProject = (int) Math.round(avgProject);
            int roundedMidExam = (int) Math.round(avgMidExam);
            int roundedFinalExam = (int) Math.round(avgFinalExam);

            double weightedPA = (roundedPA / 100.0) * paWeight;
            double weightedAssignment = (roundedAssignment / 100.0) * assignmentWeight;
            double weightedQuiz = (roundedQuiz / 100.0) * quizWeight;
            double weightedProject = (roundedProject / 100.0) * projectWeight;
            double weightedMidExam = (roundedMidExam / 100.0) * midExamWeight;
            double weightedFinalExam = (roundedFinalExam / 100.0) * finalExamWeight;

            double finalScore = weightedPA + weightedAssignment + weightedQuiz + weightedProject + weightedMidExam + weightedFinalExam;

            sb.append("Perolehan Nilai:\n");
            
            // PERBAIKAN: Gunakan Locale.US (untuk desimal '.') dan \n (untuk line ending)
            sb.append(String.format(Locale.US, ">> Partisipatif: %d/100 (%.2f/%d)\n", roundedPA, weightedPA, paWeight));
            sb.append(String.format(Locale.US, ">> Tugas: %d/100 (%.2f/%d)\n", roundedAssignment, weightedAssignment, assignmentWeight));
            sb.append(String.format(Locale.US, ">> Kuis: %d/100 (%.2f/%d)\n", roundedQuiz, weightedQuiz, quizWeight));
            sb.append(String.format(Locale.US, ">> Proyek: %d/100 (%.2f/%d)\n", roundedProject, weightedProject, projectWeight));
            sb.append(String.format(Locale.US, ">> UTS: %d/100 (%.2f/%d)\n", roundedMidExam, weightedMidExam, midExamWeight));
            sb.append(String.format(Locale.US, ">> UAS: %d/100 (%.2f/%d)\n", roundedFinalExam, weightedFinalExam, finalExamWeight));
            sb.append("\n");
            sb.append(String.format(Locale.US, ">> Nilai Akhir: %.2f\n", finalScore));
            sb.append(String.format(Locale.US, ">> Grade: %s\n", getGrade(finalScore)));
        }
        return sb.toString().trim();
    }

    /**
     * Logika inti untuk Studi Kasus 3: Perbedaan L Matriks.
     *
     * @param input String dekode yang berisi data matriks.
     * @return String hasil format analisis matriks.
     */
    private String processMatrix(String input) {
        StringBuilder sb = new StringBuilder();
        try (Scanner scanner = new Scanner(input)) {
            int matrixSize = scanner.nextInt();
            int[][] matrix = new int[matrixSize][matrixSize];
            for (int i = 0; i < matrixSize; i++) {
                for (int j = 0; j < matrixSize; j++) {
                    matrix[i][j] = scanner.nextInt();
                }
            }

            if (matrixSize == 1) {
                int centerValue = matrix[0][0];
                sb.append("Nilai L: Tidak Ada\n");
                sb.append("Nilai Kebalikan L: Tidak Ada\n");
                sb.append("Nilai Tengah: ").append(centerValue).append("\n");
                sb.append("Perbedaan: Tidak Ada\n");
                sb.append("Dominan: ").append(centerValue);
                return sb.toString();
            }

            if (matrixSize == 2) {
                int sum = 0;
                for (int i = 0; i < 2; i++) {
                    for (int j = 0; j < 2; j++) {
                        sum += matrix[i][j];
                    }
                }
                sb.append("Nilai L: Tidak Ada\n");
                sb.append("Nilai Kebalikan L: Tidak Ada\n");
                sb.append("Nilai Tengah: ").append(sum).append("\n");
                sb.append("Perbedaan: Tidak Ada\n");
                sb.append("Dominan: ").append(sum);
                return sb.toString();
            }

            int lValue = 0;
            for (int i = 0; i < matrixSize; i++) {
                lValue += matrix[i][0];
            }
            for (int j = 1; j < matrixSize - 1; j++) {
                lValue += matrix[matrixSize - 1][j];
            }

            int reverseLValue = 0;
            for (int i = 0; i < matrixSize; i++) {
                reverseLValue += matrix[i][matrixSize - 1];
            }
            for (int j = 1; j < matrixSize - 1; j++) {
                reverseLValue += matrix[0][j];
            }

            int centerValue;
            if (matrixSize % 2 == 1) {
                centerValue = matrix[matrixSize / 2][matrixSize / 2];
            } else {
                int mid1 = matrixSize / 2 - 1;
                int mid2 = matrixSize / 2;
                centerValue = matrix[mid1][mid1] + matrix[mid1][mid2] + matrix[mid2][mid1] + matrix[mid2][mid2];
            }

            int difference = Math.abs(lValue - reverseLValue);
            int dominant = (difference == 0) ? centerValue : Math.max(lValue, reverseLValue);

            sb.append("Nilai L: ").append(lValue).append(":\n");
            sb.append("Nilai Kebalikan L: ").append(reverseLValue).append("\n");
            sb.append("Nilai Tengah: ").append(centerValue).append("\n");
            sb.append("Perbedaan: ").append(difference).append("\n");
            sb.append("Dominan: ").append(dominant);
        }
        return sb.toString().trim();
    }

    /**
     * Logika inti untuk Studi Kasus 4: Analisis Frekuensi.
     *
     * @param input String dekode yang berisi data angka.
     * @return String hasil format analisis.
     */
    private String processPalingTer(String input) {
        StringBuilder sb = new StringBuilder();
        try (Scanner sc = new Scanner(input)) {
            List<Integer> numbers = new ArrayList<>();
            while (sc.hasNextInt()) {
                numbers.add(sc.nextInt());
            }

            if (numbers.isEmpty()) {
                sb.append("Tidak ada input");
                return sb.toString();
            }

            Map<Integer, Integer> freq = new LinkedHashMap<>();
            int maxVal = Integer.MIN_VALUE, minVal = Integer.MAX_VALUE;
            int mostVal = 0, mostCount = 0;

            for (int x : numbers) {
                freq.put(x, freq.getOrDefault(x, 0) + 1);
                int cNow = freq.get(x);
                if (cNow > mostCount) {
                    mostCount = cNow;
                    mostVal = x;
                }
                if (x > maxVal) maxVal = x;
                if (x < minVal) minVal = x;
            }

            Set<Integer> eliminated = new HashSet<>();
            int tersedikit = -1;
            int i = 0;
            while (i < numbers.size()) {
                int current = numbers.get(i);
                if (eliminated.contains(current)) {
                    i++;
                    continue;
                }
                int j = i + 1;
                while (j < numbers.size() && numbers.get(j) != current) {
                    j++;
                }
                if (j < numbers.size()) {
                    for (int k = i + 1; k < j; k++) {
                        eliminated.add(numbers.get(k));
                    }
                    eliminated.add(current);
                    i = j + 1;
                } else {
                    tersedikit = current;
                    break;
                }
            }

            if (tersedikit == -1) {
                sb.append("Tidak ada angka unik");
                return sb.toString();
            }

            int jtVal = -1, jtCount = -1;
            long jtProd = Long.MIN_VALUE;
            for (Map.Entry<Integer, Integer> e : freq.entrySet()) {
                int v = e.getKey(), c = e.getValue();
                long prod = (long) v * c;
                if (prod > jtProd || (prod == jtProd && v > jtVal)) {
                    jtProd = prod;
                    jtVal = v;
                    jtCount = c;
                }
            }

            int jrVal = minVal;
            int jrCount = freq.get(minVal);
            long jrProd = (long) jrVal * jrCount;

            sb.append("Tertinggi: ").append(maxVal).append("\n");
            sb.append("Terendah: ").append(minVal).append("\n");
            sb.append("Terbanyak: ").append(mostVal).append(" (").append(mostCount).append("x)\n");
            sb.append("Tersedikit: ").append(tersedikit).append(" (").append(freq.get(tersedikit)).append("x)\n");
            sb.append("Jumlah Tertinggi: ").append(jtVal).append(" * ").append(jtCount).append(" = ").append(jtProd).append("\n");
            sb.append("Jumlah Terendah: ").append(jrVal).append(" * ").append(jrCount).append(" = ").append(jrProd);
        }
        return sb.toString().trim();
    }
}
//update 