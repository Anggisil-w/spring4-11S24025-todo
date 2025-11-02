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
import java.util.NoSuchElementException; 
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
            Map.entry("13S", "Sarjana Teknik Elektro"),
            Map.entry("21S", "Sarjana Manajemen Rekayasa"),
            Map.entry("22S", "Sarjana Teknik Metalurgi"),
            Map.entry("31S", "Sarjana Teknik Bioproses"),
            Map.entry("114", "Diploma 4 Teknologi Rekasaya Perangkat Lunak"),
            Map.entry("113", "Diploma 3 Teknologi Informasi"),
            Map.entry("133", "Diploma 3 Teknologi Komputer")
    );


    // --- Endpoint Bawaan (Web Layer) ---

    @GetMapping("/") 
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
            String processedInfo = processNimInfo(nim); // Variabel diubah
            return ResponseEntity.ok(processedInfo);
        } catch (IllegalArgumentException validationError) { // Variabel diubah
            // Tangkap semua error validasi (panjang, prefix, atau format angka)
            return new ResponseEntity<>(validationError.getMessage(), HttpStatus.BAD_REQUEST);
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
            String base64DecodedData = decodeBase64(strBase64); // Variabel diubah
            String gradeResult = processNilai(base64DecodedData); // Variabel diubah
            return ResponseEntity.ok(gradeResult);
        } catch (NoSuchElementException | ArrayIndexOutOfBoundsException | NumberFormatException parsingError) { // Variabel diubah
            // Blok catch spesifik (termasuk NumberFormatException) didahulukan
            return new ResponseEntity<>("Format data input tidak valid atau tidak lengkap. Pastikan angka dan format sudah benar.", HttpStatus.BAD_REQUEST);
        } catch (IllegalArgumentException base64Error) { // Variabel diubah
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
            String base64DecodedMatrix = decodeBase64(strBase64); // Variabel diubah
            String matrixAnalysis = processMatrix(base64DecodedMatrix); // Variabel diubah
            return ResponseEntity.ok(matrixAnalysis);
        } catch (IllegalArgumentException base64Error) { // Variabel diubah
            return new ResponseEntity<>("Input Base64 tidak valid.", HttpStatus.BAD_REQUEST);
        } catch (NoSuchElementException parsingError) { // Variabel diubah
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
            String base64DecodedNumbers = decodeBase64(strBase64); // Variabel diubah
            String frequencyAnalysis = processPalingTer(base64DecodedNumbers); // Variabel diubah
            return ResponseEntity.ok(frequencyAnalysis);
        } catch (IllegalArgumentException base64Error) { // Variabel diubah
            return new ResponseEntity<>("Input Base64 tidak valid.", HttpStatus.BAD_REQUEST);
        }
        // Blok catch (NoSuchElementException e) dihapus karena unreachable.
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
            byte[] byteData = Base64.getDecoder().decode(strBase64); // Variabel diubah
            return new String(byteData, StandardCharsets.UTF_8);
        } catch (Exception base64DecodeException) { // Variabel diubah
            // Melempar exception yang lebih spesifik untuk ditangkap oleh endpoint
            throw new IllegalArgumentException("Input Base64 tidak valid: " + base64DecodeException.getMessage());
        }
    }

    /**
     * Helper method untuk mengonversi skor numerik menjadi nilai huruf (Grade).
     *
     * @param finalScore Nilai akhir (double). // Variabel diubah
     * @return String yang merepresentasikan Grade (A, AB, B, ... E).
     */
    private String getGrade(double finalScore) { // Variabel diubah
        if (finalScore >= GRADE_A_THRESHOLD) return "A";
        else if (finalScore >= GRADE_AB_THRESHOLD) return "AB";
        else if (finalScore >= GRADE_B_THRESHOLD) return "B";
        else if (finalScore >= GRADE_BC_THRESHOLD) return "BC";
        else if (finalScore >= GRADE_C_THRESHOLD) return "C";
        else if (finalScore >= GRADE_D_THRESHOLD) return "D";
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
        StringBuilder outputBuilder = new StringBuilder(); // Variabel diubah

        if (nim.length() != 8) {
            throw new IllegalArgumentException("Format NIM tidak valid. Harap masukkan 8 digit.");
        }

        String nimPrefix = nim.substring(0, 3); // Variabel diubah
        String cohortStr = nim.substring(3, 5); // Variabel diubah
        String sequenceNumber = nim.substring(5); // Variabel diubah
        String programName = PROGRAM_STUDI_MAP.get(nimPrefix); // Variabel diubah

        if (programName != null) {
            // NumberFormatException (subclass dari IllegalArgumentException) akan ditangkap oleh endpoint
            int cohortYear = 2000 + Integer.parseInt(cohortStr); // Variabel diubah
            outputBuilder.append("Inforamsi NIM ").append(nim).append(": \n");
            outputBuilder.append(">> Program Studi: ").append(programName).append("\n");
            outputBuilder.append(">> Angkatan: ").append(cohortYear).append("\n");
            outputBuilder.append(">> Urutan: ").append(Integer.parseInt(sequenceNumber));
        } else {
            throw new IllegalArgumentException("Prefix NIM '" + nimPrefix + "' tidak ditemukan.");
        }
        return outputBuilder.toString();
    }

    /**
     * Logika inti untuk Studi Kasus 2: Perolehan Nilai.
     *
     * @param input String dekode yang berisi data nilai.
     * @return String hasil format perolehan nilai.
     */
    private String processNilai(String input) {
        StringBuilder scoreResultBuilder = new StringBuilder(); // Variabel diubah
        try (Scanner inputScanner = new Scanner(input)) { // Variabel diubah
            inputScanner.useLocale(Locale.US);

            int weightPA = inputScanner.nextInt(); // Variabel diubah
            int weightAssignment = inputScanner.nextInt(); // Variabel diubah
            int weightQuiz = inputScanner.nextInt(); // Variabel diubah
            int weightProject = inputScanner.nextInt(); // Variabel diubah
            int weightMidExam = inputScanner.nextInt(); // Variabel diubah
            int weightFinalExam = inputScanner.nextInt(); // Variabel diubah
            inputScanner.nextLine();

            int totalScorePA = 0, maxScorePA = 0; // Variabel diubah
            int totalScoreAssignment = 0, maxScoreAssignment = 0; // Variabel diubah
            int totalScoreQuiz = 0, maxScoreQuiz = 0; // Variabel diubah
            int totalScoreProject = 0, maxScoreProject = 0; // Variabel diubah
            int totalScoreMidExam = 0, maxScoreMidExam = 0; // Variabel diubah
            int totalScoreFinalExam = 0, maxScoreFinalExam = 0; // Variabel diubah

            while (inputScanner.hasNextLine()) {
                String currentLine = inputScanner.nextLine().trim(); // Variabel diubah
                if (currentLine.equals("---")) break;

                String[] scoreParts = currentLine.split("\\|"); // Variabel diubah
                String categorySymbol = scoreParts[0]; // Variabel diubah
                int maxPoint = Integer.parseInt(scoreParts[1]); // Variabel diubah
                int earnedPoint = Integer.parseInt(scoreParts[2]); // Variabel diubah

                switch (categorySymbol) {
                    case "PA": maxScorePA += maxPoint; totalScorePA += earnedPoint; break;
                    case "T": maxScoreAssignment += maxPoint; totalScoreAssignment += earnedPoint; break;
                    case "K": maxScoreQuiz += maxPoint; totalScoreQuiz += earnedPoint; break;
                    case "P": maxScoreProject += maxPoint; totalScoreProject += earnedPoint; break;
                    case "UTS": maxScoreMidExam += maxPoint; totalScoreMidExam += earnedPoint; break;
                    case "UAS": maxScoreFinalExam += maxPoint; totalScoreFinalExam += earnedPoint; break;
                    default: break;
                }
            }

            double averagePA = (maxScorePA == 0) ? 0 : (totalScorePA * 100.0 / maxScorePA); // Variabel diubah
            double averageAssignment = (maxScoreAssignment == 0) ? 0 : (totalScoreAssignment * 100.0 / maxScoreAssignment); // Variabel diubah
            double averageQuiz = (maxScoreQuiz == 0) ? 0 : (totalScoreQuiz * 100.0 / maxScoreQuiz); // Variabel diubah
            double averageProject = (maxScoreProject == 0) ? 0 : (totalScoreProject * 100.0 / maxScoreProject); // Variabel diubah
            double averageMidExam = (maxScoreMidExam == 0) ? 0 : (totalScoreMidExam * 100.0 / maxScoreMidExam); // Variabel diubah
            double averageFinalExam = (maxScoreFinalExam == 0) ? 0 : (totalScoreFinalExam * 100.0 / maxScoreFinalExam); // Variabel diubah

            int finalScorePA = (int) Math.round(averagePA); // Variabel diubah
            int finalScoreAssignment = (int) Math.round(averageAssignment); // Variabel diubah
            int finalScoreQuiz = (int) Math.round(averageQuiz); // Variabel diubah
            int finalScoreProject = (int) Math.round(averageProject); // Variabel diubah
            int finalScoreMidExam = (int) Math.round(averageMidExam); // Variabel diubah
            int finalScoreFinalExam = (int) Math.round(averageFinalExam); // Variabel diubah

            double weightedScorePA = (finalScorePA / 100.0) * weightPA; // Variabel diubah
            double weightedScoreAssignment = (finalScoreAssignment / 100.0) * weightAssignment; // Variabel diubah
            double weightedScoreQuiz = (finalScoreQuiz / 100.0) * weightQuiz; // Variabel diubah
            double weightedScoreProject = (finalScoreProject / 100.0) * weightProject; // Variabel diubah
            double weightedScoreMidExam = (finalScoreMidExam / 100.0) * weightMidExam; // Variabel diubah
            double weightedScoreFinalExam = (finalScoreFinalExam / 100.0) * weightFinalExam; // Variabel diubah

            double calculatedFinalScore = weightedScorePA + weightedScoreAssignment + weightedScoreQuiz + weightedScoreProject + weightedScoreMidExam + weightedScoreFinalExam; // Variabel diubah

            scoreResultBuilder.append("Perolehan Nilai:\n");
            
            // PERBAIKAN: Gunakan Locale.US (untuk desimal '.') dan \n (untuk line ending)
            scoreResultBuilder.append(String.format(Locale.US, ">> Partisipatif: %d/100 (%.2f/%d)\n", finalScorePA, weightedScorePA, weightPA));
            scoreResultBuilder.append(String.format(Locale.US, ">> Tugas: %d/100 (%.2f/%d)\n", finalScoreAssignment, weightedScoreAssignment, weightAssignment));
            scoreResultBuilder.append(String.format(Locale.US, ">> Kuis: %d/100 (%.2f/%d)\n", finalScoreQuiz, weightedScoreQuiz, weightQuiz));
            scoreResultBuilder.append(String.format(Locale.US, ">> Proyek: %d/100 (%.2f/%d)\n", finalScoreProject, weightedScoreProject, weightProject));
            scoreResultBuilder.append(String.format(Locale.US, ">> UTS: %d/100 (%.2f/%d)\n", finalScoreMidExam, weightedScoreMidExam, weightMidExam));
            scoreResultBuilder.append(String.format(Locale.US, ">> UAS: %d/100 (%.2f/%d)\n", finalScoreFinalExam, weightedScoreFinalExam, weightFinalExam));
            scoreResultBuilder.append("\n");
            scoreResultBuilder.append(String.format(Locale.US, ">> Nilai Akhir: %.2f\n", calculatedFinalScore));
            scoreResultBuilder.append(String.format(Locale.US, ">> Grade: %s\n", getGrade(calculatedFinalScore)));
        }
        return scoreResultBuilder.toString().trim();
    }

    /**
     * Logika inti untuk Studi Kasus 3: Perbedaan L Matriks.
     *
     * @param input String dekode yang berisi data matriks.
     * @return String hasil format analisis matriks.
     */
    private String processMatrix(String input) {
        StringBuilder matrixOutput = new StringBuilder(); // Variabel diubah
        try (Scanner matrixScanner = new Scanner(input)) { // Variabel diubah
            int sizeN = matrixScanner.nextInt(); // Variabel diubah
            int[][] dataMatrix = new int[sizeN][sizeN]; // Variabel diubah
            for (int i = 0; i < sizeN; i++) {
                for (int j = 0; j < sizeN; j++) {
                    dataMatrix[i][j] = matrixScanner.nextInt();
                }
            }

            if (sizeN == 1) {
                int midValue = dataMatrix[0][0]; // Variabel diubah
                matrixOutput.append("Nilai L: Tidak Ada\n");
                matrixOutput.append("Nilai Kebalikan L: Tidak Ada\n");
                matrixOutput.append("Nilai Tengah: ").append(midValue).append("\n");
                matrixOutput.append("Perbedaan: Tidak Ada\n");
                matrixOutput.append("Dominan: ").append(midValue);
                return matrixOutput.toString();
            }

            if (sizeN == 2) {
                int totalSum = 0; // Variabel diubah
                for (int i = 0; i < 2; i++) {
                    for (int j = 0; j < 2; j++) {
                        totalSum += dataMatrix[i][j];
                    }
                }
                matrixOutput.append("Nilai L: Tidak Ada\n");
                matrixOutput.append("Nilai Kebalikan L: Tidak Ada\n");
                matrixOutput.append("Nilai Tengah: ").append(totalSum).append("\n");
                matrixOutput.append("Perbedaan: Tidak Ada\n");
                matrixOutput.append("Dominan: ").append(totalSum);
                return matrixOutput.toString();
            }

            int lPatternSum = 0; // Variabel diubah
            for (int i = 0; i < sizeN; i++) {
                lPatternSum += dataMatrix[i][0];
            }
            for (int j = 1; j < sizeN - 1; j++) {
                lPatternSum += dataMatrix[sizeN - 1][j];
            }

            int reverseLPatternSum = 0; // Variabel diubah
            for (int i = 0; i < sizeN; i++) {
                reverseLPatternSum += dataMatrix[i][sizeN - 1];
            }
            for (int j = 1; j < sizeN - 1; j++) {
                reverseLPatternSum += dataMatrix[0][j];
            }

            int midValue; // Variabel diubah
            if (sizeN % 2 == 1) {
                midValue = dataMatrix[sizeN / 2][sizeN / 2];
            } else {
                int mid1 = sizeN / 2 - 1;
                int mid2 = sizeN / 2;
                midValue = dataMatrix[mid1][mid1] + dataMatrix[mid1][mid2] + dataMatrix[mid2][mid1] + dataMatrix[mid2][mid2];
            }

            int sumDifference = Math.abs(lPatternSum - reverseLPatternSum); // Variabel diubah
            int dominantValue = (sumDifference == 0) ? midValue : Math.max(lPatternSum, reverseLPatternSum); // Variabel diubah

            matrixOutput.append("Nilai L: ").append(lPatternSum).append(":\n");
            matrixOutput.append("Nilai Kebalikan L: ").append(reverseLPatternSum).append("\n");
            matrixOutput.append("Nilai Tengah: ").append(midValue).append("\n");
            matrixOutput.append("Perbedaan: ").append(sumDifference).append("\n");
            matrixOutput.append("Dominan: ").append(dominantValue);
        }
        return matrixOutput.toString().trim();
    }

    /**
     * Logika inti untuk Studi Kasus 4: Analisis Frekuensi.
     *
     * @param input String dekode yang berisi data angka.
     * @return String hasil format analisis.
     */
    private String processPalingTer(String input) {
        StringBuilder analysisResult = new StringBuilder(); // Variabel diubah
        try (Scanner inputScanner = new Scanner(input)) { // Variabel diubah
            List<Integer> numberList = new ArrayList<>(); // Variabel diubah
            while (inputScanner.hasNextInt()) {
                numberList.add(inputScanner.nextInt());
            }

            if (numberList.isEmpty()) {
                analysisResult.append("Tidak ada input");
                return analysisResult.toString();
            }

            Map<Integer, Integer> frequencyMap = new LinkedHashMap<>(); // Variabel diubah
            int maximumValue = Integer.MIN_VALUE, minimumValue = Integer.MAX_VALUE; // Variabel diubah
            int mostFrequentValue = 0, mostFrequentCount = 0; // Variabel diubah

            for (int currentNum : numberList) { // Variabel diubah
                frequencyMap.put(currentNum, frequencyMap.getOrDefault(currentNum, 0) + 1);
                int currentCount = frequencyMap.get(currentNum); // Variabel diubah
                if (currentCount > mostFrequentCount) {
                    mostFrequentCount = currentCount;
                    mostFrequentValue = currentNum;
                }
                if (currentNum > maximumValue) maximumValue = currentNum;
                if (currentNum < minimumValue) minimumValue = currentNum;
            }

            Set<Integer> removedNumbers = new HashSet<>(); // Variabel diubah
            int leastFrequentUnique = -1; // Variabel diubah
            int i = 0;
            while (i < numberList.size()) {
                int currentIterationNum = numberList.get(i); // Variabel diubah
                if (removedNumbers.contains(currentIterationNum)) {
                    i++;
                    continue;
                }
                int j = i + 1;
                while (j < numberList.size() && numberList.get(j) != currentIterationNum) {
                    j++;
                }
                if (j < numberList.size()) {
                    for (int k = i + 1; k < j; k++) {
                        removedNumbers.add(numberList.get(k));
                    }
                    removedNumbers.add(currentIterationNum);
                    i = j + 1;
                } else {
                    leastFrequentUnique = currentIterationNum;
                    break;
                }
            }

            if (leastFrequentUnique == -1) {
                analysisResult.append("Tidak ada angka unik");
                return analysisResult.toString();
            }

            int highestProductValue = -1, highestProductCount = -1; // Variabel diubah
            long highestProduct = Long.MIN_VALUE; // Variabel diubah
            for (Map.Entry<Integer, Integer> entry : frequencyMap.entrySet()) { // Variabel diubah
                int value = entry.getKey(), count = entry.getValue(); // Variabel diubah
                long product = (long) value * count; // Variabel diubah
                if (product > highestProduct || (product == highestProduct && value > highestProductValue)) {
                    highestProduct = product;
                    highestProductValue = value;
                    highestProductCount = count;
                }
            }

            int lowestProductValue = minimumValue; // Variabel diubah
            int lowestProductCount = frequencyMap.get(minimumValue); // Variabel diubah
            long lowestProduct = (long) lowestProductValue * lowestProductCount; // Variabel diubah

            analysisResult.append("Tertinggi: ").append(maximumValue).append("\n");
            analysisResult.append("Terendah: ").append(minimumValue).append("\n");
            analysisResult.append("Terbanyak: ").append(mostFrequentValue).append(" (").append(mostFrequentCount).append("x)\n");
            analysisResult.append("Tersedikit: ").append(leastFrequentUnique).append(" (").append(frequencyMap.get(leastFrequentUnique)).append("x)\n");
            analysisResult.append("Jumlah Tertinggi: ").append(highestProductValue).append(" * ").append(highestProductCount).append(" = ").append(highestProduct).append("\n");
            analysisResult.append("Jumlah Terendah: ").append(lowestProductValue).append(" * ").append(lowestProductCount).append(" = ").append(lowestProduct);
        }
        return analysisResult.toString().trim();
    }
}
// EOF