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
    public String sayHello(@PathVariable final String name) {
        return "Hello, " + name + "!";
    }

    // --- Endpoint Studi Kasus (Web Layer) ---

    @GetMapping("/informasiNim/{nim}")
    public ResponseEntity<String> informasiNim(@PathVariable final String nim) {
        try {
            final String processedInfo = processNimInfo(nim);
            return ResponseEntity.ok(processedInfo);
        } catch (final IllegalArgumentException validationError) {
            return new ResponseEntity<>(validationError.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/perolehanNilai")
    public ResponseEntity<String> perolehanNilai(@RequestParam final String strBase64) {
        try {
            final String base64DecodedData = decodeBase64(strBase64);
            final String gradeResult = processNilai(base64DecodedData);
            return ResponseEntity.ok(gradeResult);
        } catch (final NoSuchElementException | ArrayIndexOutOfBoundsException | NumberFormatException parsingError) {
            return new ResponseEntity<>("Format data input tidak valid atau tidak lengkap. Pastikan angka dan format sudah benar.", HttpStatus.BAD_REQUEST);
        } catch (final IllegalArgumentException base64Error) {
            return new ResponseEntity<>("Input Base64 tidak valid.", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/perbedaanL")
    public ResponseEntity<String> perbedaanL(@RequestParam final String strBase64) {
        try {
            final String base64DecodedMatrix = decodeBase64(strBase64);
            final String matrixAnalysis = processMatrix(base64DecodedMatrix);
            return ResponseEntity.ok(matrixAnalysis);
        } catch (final IllegalArgumentException base64Error) {
            return new ResponseEntity<>("Input Base64 tidak valid.", HttpStatus.BAD_REQUEST);
        } catch (final NoSuchElementException parsingError) {
            return new ResponseEntity<>("Format data matriks tidak valid atau tidak lengkap.", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/palingTer")
    public ResponseEntity<String> palingTer(@RequestParam final String strBase64) {
        try {
            final String base64DecodedNumbers = decodeBase64(strBase64);
            final String frequencyAnalysis = processPalingTer(base64DecodedNumbers);
            return ResponseEntity.ok(frequencyAnalysis);
        } catch (final IllegalArgumentException base64Error) {
            return new ResponseEntity<>("Input Base64 tidak valid.", HttpStatus.BAD_REQUEST);
        }
    }


    // --- Helper & Utility Methods (Private) ---

    private String decodeBase64(final String strBase64) {
        try {
            final byte[] byteData = Base64.getDecoder().decode(strBase64);
            return new String(byteData, StandardCharsets.UTF_8);
        } catch (final IllegalArgumentException base64DecodeException) {
            throw new IllegalArgumentException("Input Base64 tidak valid: " + base64DecodeException.getMessage());
        }
    }

    private String getGrade(final double finalScore) {
        if (finalScore >= GRADE_A_THRESHOLD) return "A";
        else if (finalScore >= GRADE_AB_THRESHOLD) return "AB";
        else if (finalScore >= GRADE_B_THRESHOLD) return "B";
        else if (finalScore >= GRADE_BC_THRESHOLD) return "BC";
        else if (finalScore >= GRADE_C_THRESHOLD) return "C";
        else if (finalScore >= GRADE_D_THRESHOLD) return "D";
        else return "E";
    }

    // --- Business Logic Methods (Private "Service Layer") ---

    private String processNimInfo(final String nim) {
        final StringBuilder outputBuilder = new StringBuilder();

        if (nim.length() != 8) {
            throw new IllegalArgumentException("Format NIM tidak valid. Harap masukkan 8 digit.");
        }

        final String nimPrefix = nim.substring(0, 3);
        final String cohortStr = nim.substring(3, 5);
        final String sequenceNumber = nim.substring(5);
        final String programName = PROGRAM_STUDI_MAP.get(nimPrefix);

        if (programName != null) {
            final int cohortYear = 2000 + Integer.parseInt(cohortStr);
            outputBuilder.append("Inforamsi NIM ").append(nim).append(": \n");
            outputBuilder.append(">> Program Studi: ").append(programName).append("\n");
            outputBuilder.append(">> Angkatan: ").append(cohortYear).append("\n");
            outputBuilder.append(">> Urutan: ").append(Integer.parseInt(sequenceNumber));
        } else {
            throw new IllegalArgumentException("Prefix NIM '" + nimPrefix + "' tidak ditemukan.");
        }
        return outputBuilder.toString();
    }

// ... (Bagian di HomeController.java) ...

    /**
     * Logika inti untuk Studi Kasus 2: Perolehan Nilai.
     */
    private String processNilai(final String input) {
        final StringBuilder scoreResultBuilder = new StringBuilder();
        final String trimmedInput = input.trim();
        final String[] lines = trimmedInput.split("\\R"); 
        
        // Mengecek array kosong ATAU jika array hanya berisi string kosong 
        // (yang merupakan kasus dari input " " setelah split)
        // Jika input.trim().isEmpty() adalah TRUE, maka lines.length pasti 0.
        // Namun karena split("\\R") pada string kosong menghasilkan array [""] (length=1),
        // kita pakai pengecekan yang lebih robust.
        if (trimmedInput.isEmpty()) { 
             throw new NoSuchElementException("Data bobot tidak ditemukan.");
        }
        
        // Karena kita sudah memastikan trimmedInput tidak kosong, kita hanya perlu 
        // melanjutkan dengan array lines.
        
        try {
            // Membaca Bobot (Line 1)
            final String[] weightTokens = lines[0].trim().split("\\s+");
            if (weightTokens.length < 6) {
                throw new NoSuchElementException("Data bobot tidak lengkap.");
            }
// ... (sisa kode processNilai) ...
            final int weightPA = Integer.parseInt(weightTokens[0]);
            final int weightAssignment = Integer.parseInt(weightTokens[1]);
            final int weightQuiz = Integer.parseInt(weightTokens[2]);
            final int weightProject = Integer.parseInt(weightTokens[3]);
            final int weightMidExam = Integer.parseInt(weightTokens[4]);
            final int weightFinalExam = Integer.parseInt(weightTokens[5]);

            int totalScorePA = 0, maxScorePA = 0; 
            int totalScoreAssignment = 0, maxScoreAssignment = 0; 
            int totalScoreQuiz = 0, maxScoreQuiz = 0; 
            int totalScoreProject = 0, maxScoreProject = 0; 
            int totalScoreMidExam = 0, maxScoreMidExam = 0; 
            int totalScoreFinalExam = 0, maxScoreFinalExam = 0; 

            // Membaca Detail Nilai (Line 2 dan seterusnya)
            for (int k = 1; k < lines.length; k++) {
                final String currentLine = lines[k].trim();
                if (currentLine.equals("---")) break;
                if (currentLine.isEmpty()) continue; // Abaikan baris kosong

                final String[] scoreParts = currentLine.split("\\|"); 
                if (scoreParts.length < 3) {
                     // Ini akan menyebabkan NumberFormatException jika mencoba parse yang kurang
                     // dan akan ditangkap oleh outer try-catch di endpoint
                     throw new NumberFormatException("Baris nilai tidak lengkap.");
                }
                
                final String categorySymbol = scoreParts[0].trim();
                final int maxPoint = Integer.parseInt(scoreParts[1].trim());
                final int earnedPoint = Integer.parseInt(scoreParts[2].trim());

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
            
            // Logika perhitungan tetap sama
            final double averagePA = (maxScorePA == 0) ? 0 : (totalScorePA * 100.0 / maxScorePA);
            final double averageAssignment = (maxScoreAssignment == 0) ? 0 : (totalScoreAssignment * 100.0 / maxScoreAssignment);
            final double averageQuiz = (maxScoreQuiz == 0) ? 0 : (totalScoreQuiz * 100.0 / maxScoreQuiz);
            final double averageProject = (maxScoreProject == 0) ? 0 : (totalScoreProject * 100.0 / maxScoreProject);
            final double averageMidExam = (maxScoreMidExam == 0) ? 0 : (totalScoreMidExam * 100.0 / maxScoreMidExam);
            final double averageFinalExam = (maxScoreFinalExam == 0) ? 0 : (totalScoreFinalExam * 100.0 / maxScoreFinalExam);

            final int finalScorePA = (int) Math.round(averagePA);
            final int finalScoreAssignment = (int) Math.round(averageAssignment);
            final int finalScoreQuiz = (int) Math.round(averageQuiz);
            final int finalScoreProject = (int) Math.round(averageProject);
            final int finalScoreMidExam = (int) Math.round(averageMidExam);
            final int finalScoreFinalExam = (int) Math.round(averageFinalExam);

            final double weightedScorePA = (finalScorePA / 100.0) * weightPA;
            final double weightedScoreAssignment = (finalScoreAssignment / 100.0) * weightAssignment;
            final double weightedScoreQuiz = (finalScoreQuiz / 100.0) * weightQuiz;
            final double weightedScoreProject = (finalScoreProject / 100.0) * weightProject;
            final double weightedScoreMidExam = (finalScoreMidExam / 100.0) * weightMidExam;
            final double weightedScoreFinalExam = (finalScoreFinalExam / 100.0) * weightFinalExam;

            final double calculatedFinalScore = weightedScorePA + weightedScoreAssignment + weightedScoreQuiz + weightedScoreProject + weightedScoreMidExam + weightedScoreFinalExam;

            scoreResultBuilder.append("Perolehan Nilai:\n");
            
            scoreResultBuilder.append(String.format(Locale.US, ">> Partisipatif: %d/100 (%.2f/%d)\n", finalScorePA, weightedScorePA, weightPA));
            scoreResultBuilder.append(String.format(Locale.US, ">> Tugas: %d/100 (%.2f/%d)\n", finalScoreAssignment, weightedScoreAssignment, weightAssignment));
            scoreResultBuilder.append(String.format(Locale.US, ">> Kuis: %d/100 (%.2f/%d)\n", finalScoreQuiz, weightedScoreQuiz, weightQuiz));
            scoreResultBuilder.append(String.format(Locale.US, ">> Proyek: %d/100 (%.2f/%d)\n", finalScoreProject, weightedScoreProject, weightProject));
            scoreResultBuilder.append(String.format(Locale.US, ">> UTS: %d/100 (%.2f/%d)\n", finalScoreMidExam, weightedScoreMidExam, weightMidExam));
            scoreResultBuilder.append(String.format(Locale.US, ">> UAS: %d/100 (%.2f/%d)\n", finalScoreFinalExam, weightedScoreFinalExam, weightFinalExam));
            scoreResultBuilder.append("\n");
            scoreResultBuilder.append(String.format(Locale.US, ">> Nilai Akhir: %.2f\n", calculatedFinalScore));
            scoreResultBuilder.append(String.format(Locale.US, ">> Grade: %s\n", getGrade(calculatedFinalScore)));

        } catch (final NumberFormatException | ArrayIndexOutOfBoundsException e) {
            // Melempar kembali sebagai NoSuchElementException agar ditangkap oleh endpoint yang sesuai
            throw new NoSuchElementException("Format data bobot atau nilai detail tidak benar: " + e.getMessage());
        }

        return scoreResultBuilder.toString().trim();
    }

    /**
     * Logika inti untuk Studi Kasus 3: Perbedaan L Matriks.
     * Menggunakan String.split() untuk mendapatkan token.
     */
    private String processMatrix(final String input) {
        final StringBuilder matrixOutput = new StringBuilder();
        
        final String trimmedInput = input.trim(); 
        if (trimmedInput.isEmpty()) {
            throw new NoSuchElementException("Input matriks kosong.");
        }
        
        final String[] tokens = trimmedInput.split("\\s+");
        
        int tokenIndex = 0;
        try {
            final int sizeN = Integer.parseInt(tokens[tokenIndex++]);
            final int[][] dataMatrix = new int[sizeN][sizeN];

            for (int i = 0; i < sizeN; i++) {
                for (int j = 0; j < sizeN; j++) {
                    if (tokenIndex >= tokens.length) {
                        throw new NoSuchElementException("Data matriks tidak lengkap.");
                    }
                    dataMatrix[i][j] = Integer.parseInt(tokens[tokenIndex++]);
                }
            }

            // --- Logika perhitungan Matriks tetap sama ---
            if (sizeN == 1) {
                final int midValue = dataMatrix[0][0];
                matrixOutput.append("Nilai L: Tidak Ada\n");
                matrixOutput.append("Nilai Kebalikan L: Tidak Ada\n");
                matrixOutput.append("Nilai Tengah: ").append(midValue).append("\n");
                matrixOutput.append("Perbedaan: Tidak Ada\n");
                matrixOutput.append("Dominan: ").append(midValue);
                return matrixOutput.toString();
            }

            if (sizeN == 2) {
                int totalSum = 0; 
                for (int i = 0; i < 2; i++) {
                    for (int j = 0; j < 2; j++) {
                        totalSum += dataMatrix[i][j];
                    }
                }
                final int finalTotalSum = totalSum;
                matrixOutput.append("Nilai L: Tidak Ada\n");
                matrixOutput.append("Nilai Kebalikan L: Tidak Ada\n");
                matrixOutput.append("Nilai Tengah: ").append(finalTotalSum).append("\n");
                matrixOutput.append("Perbedaan: Tidak Ada\n");
                matrixOutput.append("Dominan: ").append(finalTotalSum);
                return matrixOutput.toString();
            }

            int lPatternSum = 0; 
            for (int i = 0; i < sizeN; i++) {
                lPatternSum += dataMatrix[i][0];
            }
            for (int j = 1; j < sizeN - 1; j++) {
                lPatternSum += dataMatrix[sizeN - 1][j];
            }
            final int finalLPatternSum = lPatternSum;

            int reverseLPatternSum = 0; 
            for (int i = 0; i < sizeN; i++) {
                reverseLPatternSum += dataMatrix[i][sizeN - 1];
            }
            for (int j = 1; j < sizeN - 1; j++) {
                reverseLPatternSum += dataMatrix[0][j];
            }
            final int finalReverseLPatternSum = reverseLPatternSum;

            final int midValue; 
            if (sizeN % 2 == 1) {
                midValue = dataMatrix[sizeN / 2][sizeN / 2];
            } else {
                final int mid1 = sizeN / 2 - 1; 
                final int mid2 = sizeN / 2;
                midValue = dataMatrix[mid1][mid1] + dataMatrix[mid1][mid2] + dataMatrix[mid2][mid1] + dataMatrix[mid2][mid2];
            }

            final int sumDifference = Math.abs(finalLPatternSum - finalReverseLPatternSum);
            final int dominantValue = (sumDifference == 0) ? midValue : Math.max(finalLPatternSum, finalReverseLPatternSum);

            matrixOutput.append("Nilai L: ").append(finalLPatternSum).append(":\n");
            matrixOutput.append("Nilai Kebalikan L: ").append(finalReverseLPatternSum).append("\n");
            matrixOutput.append("Nilai Tengah: ").append(midValue).append("\n");
            matrixOutput.append("Perbedaan: ").append(sumDifference).append("\n");
            matrixOutput.append("Dominan: ").append(dominantValue);
        
        } catch (final NumberFormatException | ArrayIndexOutOfBoundsException e) {
            throw new NoSuchElementException("Format data matriks tidak valid atau tidak lengkap.");
        }

        return matrixOutput.toString().trim();
    }

    /**
     * Logika inti untuk Studi Kasus 4: Analisis Frekuensi.
     * Menggunakan String.split() untuk mendapatkan token.
     */
    private String processPalingTer(final String input) {
        final StringBuilder analysisResult = new StringBuilder();
        
        final String[] tokenArray = input.trim().split("\\s+"); // Split by any whitespace
        final List<Integer> numberList = new ArrayList<>();

        for (final String token : tokenArray) {
            if (token.isEmpty()) continue;
            try {
                numberList.add(Integer.parseInt(token));
            } catch (final NumberFormatException e) {
            }
        }
        
        if (numberList.isEmpty()) {
            analysisResult.append("Tidak ada input");
            return analysisResult.toString();
        }

        final Map<Integer, Integer> frequencyMap = new LinkedHashMap<>();
        int maximumValue = Integer.MIN_VALUE, minimumValue = Integer.MAX_VALUE;
        int mostFrequentValue = 0, mostFrequentCount = 0; 

        for (final int currentNum : numberList) {
            frequencyMap.put(currentNum, frequencyMap.getOrDefault(currentNum, 0) + 1);
            final int currentCount = frequencyMap.get(currentNum);
            if (currentCount > mostFrequentCount) {
                mostFrequentCount = currentCount;
                mostFrequentValue = currentNum;
            }
            if (currentNum > maximumValue) maximumValue = currentNum;
            if (currentNum < minimumValue) minimumValue = currentNum;
        }

        final Set<Integer> removedNumbers = new HashSet<>();
        int leastFrequentUnique = -1;
        int i = 0;
        while (i < numberList.size()) {
            final int currentIterationNum = numberList.get(i);
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

        int highestProductValue = -1, highestProductCount = -1; 
        long highestProduct = Long.MIN_VALUE; 
        for (final Map.Entry<Integer, Integer> entry : frequencyMap.entrySet()) {
            final int value = entry.getKey();
            final int count = entry.getValue();
            final long product = (long) value * count;
            if (product > highestProduct || (product == highestProduct && value > highestProductValue)) {
                highestProduct = product;
                highestProductValue = value;
                highestProductCount = count;
            }
        }

        final int lowestProductValue = minimumValue;
        final int lowestProductCount = frequencyMap.get(minimumValue);
        final long lowestProduct = (long) lowestProductValue * lowestProductCount;

        analysisResult.append("Tertinggi: ").append(maximumValue).append("\n");
        analysisResult.append("Terendah: ").append(minimumValue).append("\n");
        analysisResult.append("Terbanyak: ").append(mostFrequentValue).append(" (").append(mostFrequentCount).append("x)\n");
        analysisResult.append("Tersedikit: ").append(leastFrequentUnique).append(" (").append(frequencyMap.get(leastFrequentUnique)).append("x)\n");
        analysisResult.append("Jumlah Tertinggi: ").append(highestProductValue).append(" * ").append(highestProductCount).append(" = ").append(highestProduct).append("\n");
        analysisResult.append("Jumlah Terendah: ").append(lowestProductValue).append(" * ").append(lowestProductCount).append(" = ").append(lowestProduct);
        
        return analysisResult.toString().trim();
    }
}
