package org.delcom.starter.controllers;

import java.util.HashMap;
import java.util.Locale;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Base64;


@RestController
public class HomeController {

    @GetMapping("/")
    public String hello() {
        return "Hay, selamat datang di aplikasi dengan Spring Boot!";
    }

    @GetMapping("/hello/{name}")
    public String sayHello(@PathVariable String name) {
        return "Hello, " + name + "!";
    }

    @GetMapping("/informasi-nim") 
        public String informasiNim(@RequestParam String nim) {
            HashMap<String, String> prodi = new HashMap<>();
            prodi.put("11S", "Sarjana Informatika");
            prodi.put("12S", "Sarjana Sistem Informasi");
            prodi.put("14S", "Sarjana Teknik Elektro");
            prodi.put("21S", "Sarjana Manajemen Rekayasa");
            prodi.put("22S", "Sarjana Teknik Metalurgi");
            prodi.put("31S", "Sarjana Teknik Bioproses");
            prodi.put("114", "Diploma 4 Teknologi Rekasaya Perangkat Lunak");
            prodi.put("113", "Diploma 3 Teknologi Informasi");
            prodi.put("133", "Diploma 3 Teknologi Komputer");

            if(nim.length() != 8) {
                return "NIM harus 8 karakter";
            }

            String degreePrefix = nim.substring(0,3);
            if(!prodi.containsKey(degreePrefix)) {
                return "Program Studi tidak Tersedia";
            }
            String angkatan = nim.substring(3, 5);
            String urutan = nim.substring(5, 8);
            String prodiPrefix = prodi.getOrDefault(degreePrefix, urutan);
           
            
            int urutanInt = Integer.parseInt(urutan); // Konversi ke integer untuk hapus leading zeros
            return String.format("Inforamsi NIM %s: >> Program Studi: %s>> Angkatan: 20%s>> Urutan: %d", nim, prodiPrefix, angkatan, urutanInt);
            
        }
    
    @GetMapping("/perolehan-nilai")
        public String perolehanNilai(@RequestParam String strBase64) {
            String decodedInput = decode(strBase64).trim();
            Locale.setDefault(Locale.US);
            String[] lines = decodedInput.split("\\R");
            int bobotPA = Integer.parseInt(lines[0].trim());
            int bobotTugas = Integer.parseInt(lines[1].trim());
            int bobotKuis = Integer.parseInt(lines[2].trim());
            int bobotProyek = Integer.parseInt(lines[3].trim());
            int bobotUTS = Integer.parseInt(lines[4].trim());
            int bobotUAS = Integer.parseInt(lines[5].trim());

            if(bobotPA + bobotTugas + bobotKuis + bobotProyek + bobotUTS + bobotUAS != 100) {
                return "Total bobot harus 100<br/>";
            }

            double totalNilaiPA=0, totalMaxPA=0;
            double totalNilaiT=0, totalMaxT=0;
            double totalNilaiK=0, totalMaxK=0;
            double totalNilaiP=0, totalMaxP=0;
            double totalNilaiUTS=0, totalMaxUTS=0;
            double totalNilaiUAS=0, totalMaxUAS=0;
            
            StringBuilder errorMsg = new StringBuilder();

            for(int i = 6; i < lines.length - 1; i++) {
                String[] parts;
                parts = lines[i].split("\\|");
                String kategori = parts[0].trim().toUpperCase();
                double max = Double.parseDouble(parts[1].trim());
                double nilai = 0;
                try {
                    if(parts.length == 2) {
                        throw new IllegalArgumentException("Data tidak valid. Silahkan menggunakan format: Simbol|Bobot|Perolehan-Nilai\nSimbol tidak dikenal\n");
                    } else {
                        nilai = Double.parseDouble(parts[2].trim());
                    }
                } catch (IllegalArgumentException e) {
                    String msg = e.getMessage().replaceAll("\n","<br/>").trim();
                    errorMsg.append(msg);
                }

                switch (kategori) {
                    case "PA": totalMaxPA += max; totalNilaiPA += nilai; break;
                    case "T" : totalMaxT  += max; totalNilaiT  += nilai; break;
                    case "K" : totalMaxK  += max; totalNilaiK  += nilai; break;
                    case "P" : totalMaxP  += max; totalNilaiP  += nilai; break;
                    case "UTS": totalMaxUTS+= max; totalNilaiUTS+= nilai; break;
                    case "UAS": totalMaxUAS+= max; totalNilaiUAS+= nilai; break;
                    default : break;
                }
            }
             // hitung persentase setiap kategori dengan pembulatan yang tepat
            double persPA  =  (int) Math.floor((double)totalNilaiPA / totalMaxPA * 100);
            double persT   =  (int) Math.floor((double)totalNilaiT  / totalMaxT  * 100);
            double persK   =  (int) Math.floor((double)totalNilaiK  / totalMaxK  * 100);
            double persP   =  (int) Math.floor((double)totalNilaiP  / totalMaxP  * 100);
            double persUTS =  (int) Math.floor((double)totalNilaiUTS/ totalMaxUTS* 100);
            double persUAS =  (int) Math.floor((double)totalNilaiUAS/ totalMaxUAS* 100);

            // kontribusi ke nilai akhir dengan pembulatan yang tepat
            double nilaiPA  = (int) Math.round(((double) persPA  / 100 * bobotPA) * 100.0) / 100.0;
            double nilaiT   = (int) Math.round(((double) persT   / 100 * bobotTugas) * 100.0) / 100.0;
            double nilaiK   = (int) Math.round(((double) persK   / 100 * bobotKuis) * 100.0) / 100.0;
            double nilaiP   = (int) Math.round(((double) persP   / 100 * bobotProyek) * 100.0) / 100.0;
            double nilaiUTS = (int) Math.round(((double) persUTS / 100 * bobotUTS) * 100.0) / 100.0;
            double nilaiUAS = (int) Math.round(((double) persUAS / 100 * bobotUAS) * 100.0) / 100.0;
            double nilaiAkhir = (int) Math.round((double)(nilaiPA + nilaiT + nilaiK + nilaiP + nilaiUTS + nilaiUAS) * 100.0) / 100.0;

            // tentukan grade
            String grade;
            if (nilaiAkhir >= 79.5) grade="A";
            else if (nilaiAkhir >= 72) grade="AB";
            else if (nilaiAkhir >= 64.5) grade="B";
            else if (nilaiAkhir >= 57) grade="BC";
            else if (nilaiAkhir >= 49.5) grade="C";
            else if (nilaiAkhir >= 34) grade="D";
            else grade="E";
            String output = String.format("Perolehan Nilai:\n>> Partisipatif: %.0f/100 (%.2f/%d)\n>> Tugas: %.0f/100 (%.2f/%d)\n>> Kuis: %.0f/100 (%.2f/%d)\n>> Proyek: %.0f/100 (%.2f/%d)\n>> UTS: %.0f/100 (%.2f/%d)\n>> UAS: %.0f/100 (%.2f/%d)\n\n>> Nilai Akhir: %.2f\n>> Grade: %s\n", persPA, nilaiPA, bobotPA, persT, nilaiT, bobotTugas, persK, nilaiK, bobotKuis, persP, nilaiP, bobotProyek, persUTS, nilaiUTS, bobotUTS, persUAS, nilaiUAS, bobotUAS, nilaiAkhir, grade);
            output = output.replaceAll("\n", "<br/>").trim();

            if(errorMsg.length() > 0) {
                return errorMsg.toString() + output;
            }

            return output;
        }

        // Helper perolehan-nilai

    


    @GetMapping("/perbedaan-l")
    public String perbedaanL(@RequestParam String strBase64) {
        String decodedInput = decode(strBase64).trim();
        
        String[] lines = decodedInput.split("\\R");
        int x = Integer.parseInt(lines[0].trim());

        int[][] a = new int[x][x];
        for(int i = 0; i < x; i++) {
            String[] nums = lines[i + 1].trim().split("\\s+");
            for(int j = 0 ; j < x; j++) {
                a[i][j] = Integer.parseInt(nums[j]);
            }
        }

        if (x == 1) {
            int angka_tengah = a[0][0];
            String output = "Nilai L: Tidak Ada\n" +
                   "Nilai Kebalikan L: Tidak Ada\n" +
                   "Nilai Tengah: " + angka_tengah + "\n" +
                   "Perbedaan: Tidak Ada\n" +
                   "Dominan: " + angka_tengah + "\n";
            output = output.replaceAll("\n", "<br/>").trim();
            return output;
        } else if (x == 2) {
            int jumlah = 0;
            for (int b = 0; b < 2; b++) {
                for (int c = 0; c < 2; c++) {
                 jumlah += a[b][c];
                }
            }
            String output = "Nilai L: Tidak Ada\n" +
                   "Nilai Kebalikan L: Tidak Ada\n" +
                   "Nilai Tengah: " + jumlah + "\n" +
                   "Perbedaan: Tidak Ada\n" +
                   "Dominan: " + jumlah + "\n";
            output = output.replaceAll("\n", "<br/>").trim();
            return output;
        } else {
            int nilai_bentuk_L = 0;
            for (int i = 0; i < x; i++) nilai_bentuk_L += a[i][0];
            for (int j = 1; j <= x - 2; j++) nilai_bentuk_L += a[x - 1][j];
    
            int nilai_kebalikan_bentuk_L = 0;
            for (int i = 0; i < x; i++) nilai_kebalikan_bentuk_L += a[i][x - 1];
            for (int j = 1; j <= x - 2; j++) nilai_kebalikan_bentuk_L += a[0][j];
    
            int angka_tengah;
            if (x % 2 == 1) {
                angka_tengah = a[x / 2][x / 2];
            } else {
                int tengah1 = x / 2 - 1;
                int tengah2 = x / 2;
                angka_tengah = a[tengah1][tengah1] + a[tengah1][tengah2] +
                               a[tengah2][tengah1] + a[tengah2][tengah2];
            }
    
            int selisih = Math.abs(nilai_bentuk_L - nilai_kebalikan_bentuk_L);
            int nilai_dominan = (selisih == 0) ? angka_tengah :
                                Math.max(nilai_bentuk_L, nilai_kebalikan_bentuk_L);
    
            String output = "Nilai L: " + nilai_bentuk_L + "\n" +
                   "Nilai Kebalikan L: " + nilai_kebalikan_bentuk_L + "\n" +
                   "Nilai Tengah: " + angka_tengah + "\n" +
                   "Perbedaan: " + selisih + "\n" +
                   "Dominan: " + nilai_dominan + "\n";
            output = output.replaceAll("\n", "<br/>").trim();
            return output;
        }
    }

    @GetMapping("/paling-ter")
    public String palingTer(@RequestParam String strBase64) {
        String decodedInput = decode(strBase64).trim();

        String[] lines = decodedInput.split("\\R");
        HashMap<Integer, Integer> hashMapCounter = new HashMap<>();
        ArrayList<Integer> daftarNilai = new ArrayList<>();
        HashMap<Integer, Integer> hashMapTotal = new HashMap<>();
        if(lines[0].equals("---")) {
            return "Informasi tidak tersedia";
        }
        for(int i = 0; i < lines.length - 1; i++) {

                int nilai = Integer.parseInt(lines[i]);
                daftarNilai.add(nilai);
    
                // Menyimpan frekuensi kemunculan
                hashMapCounter.put(nilai, hashMapCounter.getOrDefault(nilai, 0) + 1);
            
        }

        // Inisialisasi nilai awal 
        int nilaiTertinggi = 0;
        int nilaiTerendah = 1000;
        
        
        for(int nilai : daftarNilai) {

            // Total untuk setiap nilai
            int totalSekarang = hashMapTotal.getOrDefault(nilai, 0) + nilai;
            hashMapTotal.put(nilai, totalSekarang);
            
            if (nilai > nilaiTertinggi) {
                nilaiTertinggi = nilai;
            } else {
                continue;
            }
        }

        for (int nilai : daftarNilai) {
            if(nilai < nilaiTerendah) {
                nilaiTerendah = nilai;
            } else {
                continue;
            }
        }
        
        int[] arrayNilai = daftarNilai.stream().mapToInt(Integer::intValue).toArray();
        
        int nilaiJumlahTertinggi = 0;
        int nilaiJumlahTerendah = arrayNilai[0];
        int frekuensiJumlahTertinggi = 0;
        int jumlahTertinggi = 0;
        int jumlahTerendah = 0;
        jumlahTertinggi = java.util.Collections.max(hashMapTotal.values());
        jumlahTerendah = hashMapTotal.get(nilaiJumlahTerendah);
        
        for(HashMap.Entry<Integer,Integer> entry : hashMapTotal.entrySet()) {
            int nilai = entry.getKey();
            int total = entry.getValue();
            if(total == jumlahTertinggi) {
                nilaiJumlahTertinggi = nilai;
                frekuensiJumlahTertinggi = hashMapCounter.get(nilai);
            } 
            if (jumlahTerendah > total) {
                nilaiJumlahTerendah = nilai;
                jumlahTerendah = total;
            } else {
                continue;
            }
        }

        HashMap<Integer,Integer> hashMapCounterTerbanyak = new HashMap<>();
        int nilaiTerbanyak = arrayNilai[0];
        int frekuensiTerbanyak = 0;
        for(int i = 0; i < arrayNilai.length; i++) {
            hashMapCounterTerbanyak.put(arrayNilai[i], hashMapCounterTerbanyak.getOrDefault(arrayNilai[i], 0) + 1);
            int frekuensiSaatIni = hashMapCounterTerbanyak.get(arrayNilai[i]);
            if(frekuensiSaatIni > frekuensiTerbanyak ) {
                nilaiTerbanyak = arrayNilai[i];
                frekuensiTerbanyak = frekuensiSaatIni;
            }
        }

        int nilaiTersedikit = arrayNilai[0];
        HashMap<Integer,Integer> hashMapCounterTersedikit = new HashMap<>();
        int frekuensiTersedikit = 0;
        hashMapCounterTersedikit.put(nilaiTersedikit, 1);
        for(int i = 1; i < arrayNilai.length; i++) {
            hashMapCounterTersedikit.put(arrayNilai[i], hashMapCounterTersedikit.getOrDefault(arrayNilai[i], 0) + 1 );
            if(arrayNilai[i] != nilaiTersedikit) {
                continue;
            } else {
                boolean foundNewValue = false;
                for(int j = i + 1; j < arrayNilai.length; j++) {
                    if (foundNewValue) {
                        continue;
                    }
                    
                    if(!hashMapCounterTersedikit.containsKey(arrayNilai[j])) {
                        hashMapCounterTersedikit.put(arrayNilai[j], 1);
                        nilaiTersedikit = arrayNilai[j];
                        frekuensiTersedikit = hashMapCounter.get(nilaiTersedikit);
                        i = j;
                        foundNewValue = true;
                    } else {
                        continue;
                    }
                }
            }
        }
        

    String output = "";
    output += "Tertinggi: " + nilaiTertinggi + "\n";
    output += "Terendah: " + nilaiTerendah + "\n";
    output += "Terbanyak: " + nilaiTerbanyak + " " + "(" + frekuensiTerbanyak + "x)" + "\n";
    output += "Tersedikit: " + nilaiTersedikit + " " + "(" + frekuensiTersedikit + "x)" + "\n";
    output += "Jumlah Tertinggi: " + nilaiJumlahTertinggi + " * " + frekuensiJumlahTertinggi + " = " + jumlahTertinggi + "\n";
    output += "Jumlah Terendah: " + nilaiJumlahTerendah + " * " + hashMapCounter.get(nilaiJumlahTerendah) + " = " + jumlahTerendah + "\n";
    
    output = output.replaceAll("\n", "<br/>").trim();
    return output;     
    }
    
    

    // Helper 
    public static String decode(String base64) {
        return new String(Base64.getDecoder().decode(base64));
    }
}