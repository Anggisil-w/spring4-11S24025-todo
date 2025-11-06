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
    public String sayHello(@PathVariable String namaPengguna) { // name -> namaPengguna
        return "Hello, " + namaPengguna + "!"; // name -> namaPengguna
    }

    @GetMapping("/informasi-nim") 
        public String informasiNim(@RequestParam String nomorInduk) { // nim -> nomorInduk
            HashMap<String, String> daftarProdi = new HashMap<>(); // prodi -> daftarProdi
            daftarProdi.put("11S", "Sarjana Informatika"); // prodi -> daftarProdi
            daftarProdi.put("12S", "Sarjana Sistem Informasi"); // prodi -> daftarProdi
            daftarProdi.put("14S", "Sarjana Teknik Elektro"); // prodi -> daftarProdi
            daftarProdi.put("21S", "Sarjana Manajemen Rekayasa"); // prodi -> daftarProdi
            daftarProdi.put("22S", "Sarjana Teknik Metalurgi"); // prodi -> daftarProdi
            daftarProdi.put("31S", "Sarjana Teknik Bioproses"); // prodi -> daftarProdi
            daftarProdi.put("114", "Diploma 4 Teknologi Rekasaya Perangkat Lunak"); // prodi -> daftarProdi
            daftarProdi.put("113", "Diploma 3 Teknologi Informasi"); // prodi -> daftarProdi
            daftarProdi.put("133", "Diploma 3 Teknologi Komputer"); // prodi -> daftarProdi

            if(nomorInduk.length() != 8) { // nim -> nomorInduk
                return "NIM harus 8 karakter";
            }

            String kodeProdi = nomorInduk.substring(0,3); // degreePrefix -> kodeProdi, nim -> nomorInduk
            if(!daftarProdi.containsKey(kodeProdi)) { // prodi -> daftarProdi, degreePrefix -> kodeProdi
                return "Program Studi tidak Tersedia";
            }
            String tahunMasuk = nomorInduk.substring(3, 5); // angkatan -> tahunMasuk, nim -> nomorInduk
            String nomorUrut = nomorInduk.substring(5, 8); // urutan -> nomorUrut, nim -> nomorInduk
            String namaProgramStudi = daftarProdi.getOrDefault(kodeProdi, nomorUrut); // prodiPrefix -> namaProgramStudi, prodi -> daftarProdi, degreePrefix -> kodeProdi, urutan -> nomorUrut
           
            
            int urutanNumerik = Integer.parseInt(nomorUrut); // urutanInt -> urutanNumerik, urutan -> nomorUrut
            return String.format("Inforamsi NIM %s: >> Program Studi: %s>> Angkatan: 20%s>> Urutan: %d", nomorInduk, namaProgramStudi, tahunMasuk, urutanNumerik); // nim -> nomorInduk, prodiPrefix -> namaProgramStudi, angkatan -> tahunMasuk, urutanInt -> urutanNumerik
            
        }
    
    @GetMapping("/perolehan-nilai")
        public String perolehanNilai(@RequestParam String inputB64) { // strBase64 -> inputB64
            String dataTerkode = decode(inputB64).trim(); // decodedInput -> dataTerkode, strBase64 -> inputB64
            Locale.setDefault(Locale.US);
            String[] barisData = dataTerkode.split("\\R"); // lines -> barisData, decodedInput -> dataTerkode
            int persenPA = Integer.parseInt(barisData[0].trim()); // bobotPA -> persenPA, lines -> barisData
            int persenTugas = Integer.parseInt(barisData[1].trim()); // bobotTugas -> persenTugas, lines -> barisData
            int persenKuis = Integer.parseInt(barisData[2].trim()); // bobotKuis -> persenKuis, lines -> barisData
            int persenProyek = Integer.parseInt(barisData[3].trim()); // bobotProyek -> persenProyek, lines -> barisData
            int persenUTS = Integer.parseInt(barisData[4].trim()); // bobotUTS -> persenUTS, lines -> barisData
            int persenUAS = Integer.parseInt(barisData[5].trim()); // bobotUAS -> persenUAS, lines -> barisData

            if(persenPA + persenTugas + persenKuis + persenProyek + persenUTS + persenUAS != 100) { // bobotPA -> persenPA, bobotTugas -> persenTugas, bobotKuis -> persenKuis, bobotProyek -> persenProyek, bobotUTS -> persenUTS, bobotUAS -> persenUAS
                return "Total bobot harus 100<br/>";
            }

            double akumulasiNilaiPA=0, akumulasiMaxPA=0; // totalNilaiPA, totalMaxPA
            double akumulasiNilaiTugas=0, akumulasiMaxTugas=0; // totalNilaiT, totalMaxT
            double akumulasiNilaiKuis=0, akumulasiMaxKuis=0; // totalNilaiK, totalMaxK
            double akumulasiNilaiProyek=0, akumulasiMaxProyek=0; // totalNilaiP, totalMaxP
            double akumulasiNilaiUTS=0, akumulasiMaxUTS=0; // totalNilaiUTS, totalMaxUTS
            double akumulasiNilaiUAS=0, akumulasiMaxUAS=0; // totalNilaiUAS, totalMaxUAS
            
            StringBuilder pesanKesalahan = new StringBuilder(); // errorMsg -> pesanKesalahan

            for(int indeks = 6; indeks < barisData.length - 1; indeks++) { // i -> indeks, lines -> barisData
                String[] bagian; // parts -> bagian
                bagian = barisData[indeks].split("\\|"); // parts -> bagian, lines -> barisData, i -> indeks
                String jenisKomponen = bagian[0].trim().toUpperCase(); // kategori -> jenisKomponen, parts -> bagian
                double maksimum = Double.parseDouble(bagian[1].trim()); // max -> maksimum, parts -> bagian
                double perolehan = 0; // nilai -> perolehan
                try {
                    if(bagian.length == 2) { // parts -> bagian
                        throw new IllegalArgumentException("Data tidak valid. Silahkan menggunakan format: Simbol|Bobot|Perolehan-Nilai\nSimbol tidak dikenal\n");
                    } else {
                        perolehan = Double.parseDouble(bagian[2].trim()); // nilai -> perolehan, parts -> bagian
                    }
                } catch (IllegalArgumentException exceptionObj) { // e -> exceptionObj
                    String teksKesalahan = exceptionObj.getMessage().replaceAll("\n","<br/>").trim(); // msg -> teksKesalahan, e -> exceptionObj
                    pesanKesalahan.append(teksKesalahan); // errorMsg -> pesanKesalahan, msg -> teksKesalahan
                }

                switch (jenisKomponen) { // kategori -> jenisKomponen
                    case "PA": akumulasiMaxPA += maksimum; akumulasiNilaiPA += perolehan; break; // totalMaxPA, max -> maksimum, totalNilaiPA, nilai -> perolehan
                    case "T" : akumulasiMaxTugas  += maksimum; akumulasiNilaiTugas  += perolehan; break; // totalMaxT, max -> maksimum, totalNilaiT, nilai -> perolehan
                    case "K" : akumulasiMaxKuis  += maksimum; akumulasiNilaiKuis  += perolehan; break; // totalMaxK, max -> maksimum, totalNilaiK, nilai -> perolehan
                    case "P" : akumulasiMaxProyek  += maksimum; akumulasiNilaiProyek  += perolehan; break; // totalMaxP, max -> maksimum, totalNilaiP, nilai -> perolehan
                    case "UTS": akumulasiMaxUTS+= maksimum; akumulasiNilaiUTS+= perolehan; break; // totalMaxUTS, max -> maksimum, totalNilaiUTS, nilai -> perolehan
                    case "UAS": akumulasiMaxUAS+= maksimum; akumulasiNilaiUAS+= perolehan; break; // totalMaxUAS, max -> maksimum, totalNilaiUAS, nilai -> perolehan
                    default : break;
                }
            }
             // hitung persentase setiap kategori dengan pembulatan yang tepat
            double persenPartisipatif  =  (int) Math.floor((double)akumulasiNilaiPA / akumulasiMaxPA * 100); // persPA, totalNilaiPA, totalMaxPA
            double persenTugasFinal   =  (int) Math.floor((double)akumulasiNilaiTugas  / akumulasiMaxTugas  * 100); // persT, totalNilaiT, totalMaxT
            double persenKuisFinal   =  (int) Math.floor((double)akumulasiNilaiKuis  / akumulasiMaxKuis  * 100); // persK, totalNilaiK, totalMaxK
            double persenProyekFinal   =  (int) Math.floor((double)akumulasiNilaiProyek  / akumulasiMaxProyek  * 100); // persP, totalNilaiP, totalMaxP
            double persenUTSFinal =  (int) Math.floor((double)akumulasiNilaiUTS/ akumulasiMaxUTS* 100); // persUTS, totalNilaiUTS, totalMaxUTS
            double persenUASFinal =  (int) Math.floor((double)akumulasiNilaiUAS/ akumulasiMaxUAS* 100); // persUAS, totalNilaiUAS, totalMaxUAS

            // kontribusi ke nilai akhir dengan pembulatan yang tepat
            double kontribusiPA  = (int) Math.round(((double) persenPartisipatif  / 100 * persenPA) * 100.0) / 100.0; // nilaiPA, persPA, bobotPA
            double kontribusiTugas   = (int) Math.round(((double) persenTugasFinal   / 100 * persenTugas) * 100.0) / 100.0; // nilaiT, persT, bobotTugas
            double kontribusiKuis   = (int) Math.round(((double) persenKuisFinal   / 100 * persenKuis) * 100.0) / 100.0; // nilaiK, persK, bobotKuis
            double kontribusiProyek   = (int) Math.round(((double) persenProyekFinal   / 100 * persenProyek) * 100.0) / 100.0; // nilaiP, persP, bobotProyek
            double kontribusiUTS = (int) Math.round(((double) persenUTSFinal / 100 * persenUTS) * 100.0) / 100.0; // nilaiUTS, persUTS, bobotUTS
            double kontribusiUAS = (int) Math.round(((double) persenUASFinal / 100 * persenUAS) * 100.0) / 100.0; // nilaiUAS, persUAS, bobotUAS
            double skorAkhir = (int) Math.round((double)(kontribusiPA + kontribusiTugas + kontribusiKuis + kontribusiProyek + kontribusiUTS + kontribusiUAS) * 100.0) / 100.0; // nilaiAkhir, nilaiPA, nilaiT, nilaiK, nilaiP, nilaiUTS, nilaiUAS

            // tentukan grade
            String hurufMutu; // grade -> hurufMutu
            if (skorAkhir >= 79.5) hurufMutu="A"; // nilaiAkhir -> skorAkhir
            else if (skorAkhir >= 72) hurufMutu="AB"; // nilaiAkhir -> skorAkhir
            else if (skorAkhir >= 64.5) hurufMutu="B"; // nilaiAkhir -> skorAkhir
            else if (skorAkhir >= 57) hurufMutu="BC"; // nilaiAkhir -> skorAkhir
            else if (skorAkhir >= 49.5) hurufMutu="C"; // nilaiAkhir -> skorAkhir
            else if (skorAkhir >= 34) hurufMutu="D"; // nilaiAkhir -> skorAkhir
            else hurufMutu="E";
            String hasilAkhir = String.format("Perolehan Nilai:\n>> Partisipatif: %.0f/100 (%.2f/%d)\n>> Tugas: %.0f/100 (%.2f/%d)\n>> Kuis: %.0f/100 (%.2f/%d)\n>> Proyek: %.0f/100 (%.2f/%d)\n>> UTS: %.0f/100 (%.2f/%d)\n>> UAS: %.0f/100 (%.2f/%d)\n\n>> Nilai Akhir: %.2f\n>> Grade: %s\n", persenPartisipatif, kontribusiPA, persenPA, persenTugasFinal, kontribusiTugas, persenTugas, persenKuisFinal, kontribusiKuis, persenKuis, persenProyekFinal, kontribusiProyek, persenProyek, persenUTSFinal, kontribusiUTS, persenUTS, persenUASFinal, kontribusiUAS, persenUAS, skorAkhir, hurufMutu); // output, persPA, nilaiPA, bobotPA, persT, nilaiT, bobotTugas, persK, nilaiK, bobotKuis, persP, nilaiP, bobotProyek, persUTS, nilaiUTS, bobotUTS, persUAS, nilaiUAS, bobotUAS, nilaiAkhir, grade
            hasilAkhir = hasilAkhir.replaceAll("\n", "<br/>").trim(); // output -> hasilAkhir

            if(pesanKesalahan.length() > 0) { // errorMsg -> pesanKesalahan
                return pesanKesalahan.toString() + hasilAkhir; // errorMsg -> pesanKesalahan, output -> hasilAkhir
            }

            return hasilAkhir; // output -> hasilAkhir
        }

        // Helper perolehan-nilai

    


    @GetMapping("/perbedaan-l")
    public String perbedaanL(@RequestParam String inputData) { // strBase64 -> inputData
        String dataInput = decode(inputData).trim(); // decodedInput -> dataInput, strBase64 -> inputData
        
        String[] baris = dataInput.split("\\R"); // lines -> baris, decodedInput -> dataInput
        int ukuranMatriks = Integer.parseInt(baris[0].trim()); // x -> ukuranMatriks, lines -> baris

        int[][] matriksA = new int[ukuranMatriks][ukuranMatriks]; // a -> matriksA, x -> ukuranMatriks
        for(int barisIndex = 0; barisIndex < ukuranMatriks; barisIndex++) { // i -> barisIndex, x -> ukuranMatriks
            String[] angkaBaris = baris[barisIndex + 1].trim().split("\\s+"); // nums -> angkaBaris, lines -> baris, i -> barisIndex
            for(int kolomIndex = 0 ; kolomIndex < ukuranMatriks; kolomIndex++) { // j -> kolomIndex, x -> ukuranMatriks
                matriksA[barisIndex][kolomIndex] = Integer.parseInt(angkaBaris[kolomIndex]); // a -> matriksA, i -> barisIndex, j -> kolomIndex, nums -> angkaBaris, j -> kolomIndex
            }
        }

        if (ukuranMatriks == 1) { // x -> ukuranMatriks
            int nilaiPusat = matriksA[0][0]; // angka_tengah -> nilaiPusat, a -> matriksA
            String teksHasil = "Nilai L: Tidak Ada\n" + // output -> teksHasil
                   "Nilai Kebalikan L: Tidak Ada\n" +
                   "Nilai Tengah: " + nilaiPusat + "\n" + // angka_tengah -> nilaiPusat
                   "Perbedaan: Tidak Ada\n" +
                   "Dominan: " + nilaiPusat + "\n"; // angka_tengah -> nilaiPusat
            teksHasil = teksHasil.replaceAll("\n", "<br/>").trim(); // output -> teksHasil
            return teksHasil; // output -> teksHasil
        } else if (ukuranMatriks == 2) { // x -> ukuranMatriks
            int totalElemen = 0; // jumlah -> totalElemen
            for (int indeksB = 0; indeksB < 2; indeksB++) { // b -> indeksB
                for (int indeksC = 0; indeksC < 2; indeksC++) { // c -> indeksC
                 totalElemen += matriksA[indeksB][indeksC]; // jumlah -> totalElemen, a -> matriksA, b -> indeksB, c -> indeksC
                }
            }
            String teksHasil = "Nilai L: Tidak Ada\n" + // output -> teksHasil
                   "Nilai Kebalikan L: Tidak Ada\n" +
                   "Nilai Tengah: " + totalElemen + "\n" + // jumlah -> totalElemen
                   "Perbedaan: Tidak Ada\n" +
                   "Dominan: " + totalElemen + "\n"; // jumlah -> totalElemen
            teksHasil = teksHasil.replaceAll("\n", "<br/>").trim(); // output -> teksHasil
            return teksHasil; // output -> teksHasil
        } else {
            int totalBentukL = 0; // nilai_bentuk_L -> totalBentukL
            for (int barisIndex = 0; barisIndex < ukuranMatriks; barisIndex++) totalBentukL += matriksA[barisIndex][0]; // i -> barisIndex, x -> ukuranMatriks, nilai_bentuk_L -> totalBentukL, a -> matriksA, i -> barisIndex
            for (int kolomIndex = 1; kolomIndex <= ukuranMatriks - 2; kolomIndex++) totalBentukL += matriksA[ukuranMatriks - 1][kolomIndex]; // j -> kolomIndex, x -> ukuranMatriks, nilai_bentuk_L -> totalBentukL, a -> matriksA, x -> ukuranMatriks, j -> kolomIndex
    
            int totalKebalikanL = 0; // nilai_kebalikan_bentuk_L -> totalKebalikanL
            for (int barisIndex = 0; barisIndex < ukuranMatriks; barisIndex++) totalKebalikanL += matriksA[barisIndex][ukuranMatriks - 1]; // i -> barisIndex, x -> ukuranMatriks, nilai_kebalikan_bentuk_L -> totalKebalikanL, a -> matriksA, i -> barisIndex, x -> ukuranMatriks
            for (int kolomIndex = 1; kolomIndex <= ukuranMatriks - 2; kolomIndex++) totalKebalikanL += matriksA[0][kolomIndex]; // j -> kolomIndex, x -> ukuranMatriks, nilai_kebalikan_bentuk_L -> totalKebalikanL, a -> matriksA, j -> kolomIndex
    
            int nilaiPusat; // angka_tengah -> nilaiPusat
            if (ukuranMatriks % 2 == 1) { // x -> ukuranMatriks
                nilaiPusat = matriksA[ukuranMatriks / 2][ukuranMatriks / 2]; // angka_tengah -> nilaiPusat, a -> matriksA, x -> ukuranMatriks
            } else {
                int pusat1 = ukuranMatriks / 2 - 1; // tengah1 -> pusat1, x -> ukuranMatriks
                int pusat2 = ukuranMatriks / 2; // tengah2 -> pusat2, x -> ukuranMatriks
                nilaiPusat = matriksA[pusat1][pusat1] + matriksA[pusat1][pusat2] + // angka_tengah -> nilaiPusat, a -> matriksA, tengah1 -> pusat1, tengah2 -> pusat2
                               matriksA[pusat2][pusat1] + matriksA[pusat2][pusat2]; // a -> matriksA, tengah2 -> pusat2, tengah1 -> pusat1
            }
    
            int bedaL = Math.abs(totalBentukL - totalKebalikanL); // selisih -> bedaL, nilai_bentuk_L -> totalBentukL, nilai_kebalikan_bentuk_L -> totalKebalikanL
            int hasilDominan = (bedaL == 0) ? nilaiPusat : // nilai_dominan -> hasilDominan, selisih -> bedaL, angka_tengah -> nilaiPusat
                                Math.max(totalBentukL, totalKebalikanL); // nilai_bentuk_L -> totalBentukL, nilai_kebalikan_bentuk_L -> totalKebalikanL
    
            String teksHasil = "Nilai L: " + totalBentukL + "\n" + // output -> teksHasil, nilai_bentuk_L -> totalBentukL
                   "Nilai Kebalikan L: " + totalKebalikanL + "\n" + // nilai_kebalikan_bentuk_L -> totalKebalikanL
                   "Nilai Tengah: " + nilaiPusat + "\n" + // angka_tengah -> nilaiPusat
                   "Perbedaan: " + bedaL + "\n" + // selisih -> bedaL
                   "Dominan: " + hasilDominan + "\n"; // nilai_dominan -> hasilDominan
            teksHasil = teksHasil.replaceAll("\n", "<br/>").trim(); // output -> teksHasil
            return teksHasil; // output -> teksHasil
        }
    }

    @GetMapping("/paling-ter")
    public String palingTer(@RequestParam String inputString) { // strBase64 -> inputString
        String dataDidekode = decode(inputString).trim(); // decodedInput -> dataDidekode, strBase64 -> inputString

        String[] barisInput = dataDidekode.split("\\R"); // lines -> barisInput, decodedInput -> dataDidekode
        HashMap<Integer, Integer> petaFrekuensi = new HashMap<>(); // hashMapCounter -> petaFrekuensi
        ArrayList<Integer> listNilai = new ArrayList<>(); // daftarNilai -> listNilai
        HashMap<Integer, Integer> petaTotal = new HashMap<>(); // hashMapTotal -> petaTotal
        if(barisInput[0].equals("---")) { // lines -> barisInput
            return "Informasi tidak tersedia";
        }
        for(int indeksBaris = 0; indeksBaris < barisInput.length - 1; indeksBaris++) { // i -> indeksBaris, lines -> barisInput

                int angkaSaatIni = Integer.parseInt(barisInput[indeksBaris]); // nilai -> angkaSaatIni, lines -> barisInput, i -> indeksBaris
                listNilai.add(angkaSaatIni); // daftarNilai -> listNilai, nilai -> angkaSaatIni
    
                // Menyimpan frekuensi kemunculan
                petaFrekuensi.put(angkaSaatIni, petaFrekuensi.getOrDefault(angkaSaatIni, 0) + 1); // hashMapCounter -> petaFrekuensi, nilai -> angkaSaatIni
            
        }

        // Inisialisasi nilai awal 
        int maksimumNilai = 0; // nilaiTertinggi -> maksimumNilai
        int minimumNilai = 1000; // nilaiTerendah -> minimumNilai
        
        
        for(int angkaSaatIni : listNilai) { // nilai -> angkaSaatIni, daftarNilai -> listNilai

            // Total untuk setiap nilai
            int totalSementara = petaTotal.getOrDefault(angkaSaatIni, 0) + angkaSaatIni; // totalSekarang -> totalSementara, hashMapTotal -> petaTotal, nilai -> angkaSaatIni
            petaTotal.put(angkaSaatIni, totalSementara); // hashMapTotal -> petaTotal, nilai -> angkaSaatIni, totalSekarang -> totalSementara
            
            if (angkaSaatIni > maksimumNilai) { // nilai -> angkaSaatIni, nilaiTertinggi -> maksimumNilai
                maksimumNilai = angkaSaatIni; // nilaiTertinggi -> maksimumNilai, nilai -> angkaSaatIni
            } else {
                continue;
            }
        }

        for (int angkaSaatIni : listNilai) { // nilai -> angkaSaatIni, daftarNilai -> listNilai
            if(angkaSaatIni < minimumNilai) { // nilai -> angkaSaatIni, nilaiTerendah -> minimumNilai
                minimumNilai = angkaSaatIni; // nilaiTerendah -> minimumNilai, nilai -> angkaSaatIni
            } else {
                continue;
            }
        }
        
        int[] deretAngka = listNilai.stream().mapToInt(Integer::intValue).toArray(); // arrayNilai -> deretAngka, daftarNilai -> listNilai
        
        int angkaTotalMax = 0; // nilaiJumlahTertinggi -> angkaTotalMax
        int angkaTotalMin = deretAngka[0]; // nilaiJumlahTerendah -> angkaTotalMin, arrayNilai -> deretAngka
        int frekTotalMax = 0; // frekuensiJumlahTertinggi -> frekTotalMax
        int totalMaksimum = 0; // jumlahTertinggi -> totalMaksimum
        int totalMinimum = 0; // jumlahTerendah -> totalMinimum
        totalMaksimum = java.util.Collections.max(petaTotal.values()); // jumlahTertinggi -> totalMaksimum, hashMapTotal -> petaTotal
        totalMinimum = petaTotal.get(angkaTotalMin); // jumlahTerendah -> totalMinimum, hashMapTotal -> petaTotal, nilaiJumlahTerendah -> angkaTotalMin
        
        for(HashMap.Entry<Integer,Integer> pasangan : petaTotal.entrySet()) { // entry -> pasangan, hashMapTotal -> petaTotal
            int angkaSaatIni = pasangan.getKey(); // nilai -> angkaSaatIni, entry -> pasangan
            int nilaiTotal = pasangan.getValue(); // total -> nilaiTotal, entry -> pasangan
            if(nilaiTotal == totalMaksimum) { // total -> nilaiTotal, jumlahTertinggi -> totalMaksimum
                angkaTotalMax = angkaSaatIni; // nilaiJumlahTertinggi -> angkaTotalMax, nilai -> angkaSaatIni
                frekTotalMax = petaFrekuensi.get(angkaSaatIni); // frekuensiJumlahTertinggi -> frekTotalMax, hashMapCounter -> petaFrekuensi, nilai -> angkaSaatIni
            } 
            if (totalMinimum > nilaiTotal) { // jumlahTerendah -> totalMinimum, total -> nilaiTotal
                angkaTotalMin = angkaSaatIni; // nilaiJumlahTerendah -> angkaTotalMin, nilai -> angkaSaatIni
                totalMinimum = nilaiTotal; // jumlahTerendah -> totalMinimum, total -> nilaiTotal
            } else {
                continue;
            }
        }

        HashMap<Integer,Integer> petaFrekuensiMax = new HashMap<>(); // hashMapCounterTerbanyak -> petaFrekuensiMax
        int angkaTerbanyak = deretAngka[0]; // nilaiTerbanyak -> angkaTerbanyak, arrayNilai -> deretAngka
        int frekTerbanyak = 0; // frekuensiTerbanyak -> frekTerbanyak
        for(int indeksBaris = 0; indeksBaris < deretAngka.length; indeksBaris++) { // i -> indeksBaris, arrayNilai -> deretAngka
            petaFrekuensiMax.put(deretAngka[indeksBaris], petaFrekuensiMax.getOrDefault(deretAngka[indeksBaris], 0) + 1); // hashMapCounterTerbanyak -> petaFrekuensiMax, arrayNilai -> deretAngka, i -> indeksBaris
            int frekSaatIni = petaFrekuensiMax.get(deretAngka[indeksBaris]); // frekuensiSaatIni -> frekSaatIni, hashMapCounterTerbanyak -> petaFrekuensiMax, arrayNilai -> deretAngka, i -> indeksBaris
            if(frekSaatIni > frekTerbanyak ) { // frekuensiSaatIni -> frekSaatIni, frekuensiTerbanyak -> frekTerbanyak
                angkaTerbanyak = deretAngka[indeksBaris]; // nilaiTerbanyak -> angkaTerbanyak, arrayNilai -> deretAngka, i -> indeksBaris
                frekTerbanyak = frekSaatIni; // frekuensiTerbanyak -> frekTerbanyak, frekuensiSaatIni -> frekSaatIni
            }
        }

        int angkaTersedikit = deretAngka[0]; // nilaiTersedikit -> angkaTersedikit, arrayNilai -> deretAngka
        HashMap<Integer,Integer> petaFrekuensiMin = new HashMap<>(); // hashMapCounterTersedikit -> petaFrekuensiMin
        int frekTersedikit = 0; // frekuensiTersedikit -> frekTersedikit
        petaFrekuensiMin.put(angkaTersedikit, 1); // hashMapCounterTersedikit -> petaFrekuensiMin, nilaiTersedikit -> angkaTersedikit
        for(int indeksBaris = 1; indeksBaris < deretAngka.length; indeksBaris++) { // i -> indeksBaris, arrayNilai -> deretAngka
            petaFrekuensiMin.put(deretAngka[indeksBaris], petaFrekuensiMin.getOrDefault(deretAngka[indeksBaris], 0) + 1 ); // hashMapCounterTersedikit -> petaFrekuensiMin, arrayNilai -> deretAngka, i -> indeksBaris
            if(deretAngka[indeksBaris] != angkaTersedikit) { // arrayNilai -> deretAngka, i -> indeksBaris, nilaiTersedikit -> angkaTersedikit
                continue;
            } else {
                boolean nilaiBaruDitemukan = false; // foundNewValue -> nilaiBaruDitemukan
                for(int indeksJ = indeksBaris + 1; indeksJ < deretAngka.length; indeksJ++) { // j -> indeksJ, i -> indeksBaris, arrayNilai -> deretAngka
                    if (nilaiBaruDitemukan) { // foundNewValue -> nilaiBaruDitemukan
                        continue;
                    }
                    
                    if(!petaFrekuensiMin.containsKey(deretAngka[indeksJ])) { // hashMapCounterTersedikit -> petaFrekuensiMin, arrayNilai -> deretAngka, j -> indeksJ
                        petaFrekuensiMin.put(deretAngka[indeksJ], 1); // hashMapCounterTersedikit -> petaFrekuensiMin, arrayNilai -> deretAngka, j -> indeksJ
                        angkaTersedikit = deretAngka[indeksJ]; // nilaiTersedikit -> angkaTersedikit, arrayNilai -> deretAngka, j -> indeksJ
                        frekTersedikit = petaFrekuensi.get(angkaTersedikit); // frekuensiTersedikit -> frekTersedikit, hashMapCounter -> petaFrekuensi, nilaiTersedikit -> angkaTersedikit
                        indeksBaris = indeksJ; // i -> indeksBaris, j -> indeksJ
                        nilaiBaruDitemukan = true; // foundNewValue -> nilaiBaruDitemukan
                    } else {
                        continue;
                    }
                }
            }
        }
        

    String hasilFormat = ""; // output -> hasilFormat
    hasilFormat += "Tertinggi: " + maksimumNilai + "\n"; // output -> hasilFormat, nilaiTertinggi -> maksimumNilai
    hasilFormat += "Terendah: " + minimumNilai + "\n"; // output -> hasilFormat, nilaiTerendah -> minimumNilai
    hasilFormat += "Terbanyak: " + angkaTerbanyak + " " + "(" + frekTerbanyak + "x)" + "\n"; // output -> hasilFormat, nilaiTerbanyak -> angkaTerbanyak, frekuensiTerbanyak -> frekTerbanyak
    hasilFormat += "Tersedikit: " + angkaTersedikit + " " + "(" + frekTersedikit + "x)" + "\n"; // output -> hasilFormat, nilaiTersedikit -> angkaTersedikit, frekuensiTersedikit -> frekTersedikit
    hasilFormat += "Jumlah Tertinggi: " + angkaTotalMax + " * " + frekTotalMax + " = " + totalMaksimum + "\n"; // output -> hasilFormat, nilaiJumlahTertinggi -> angkaTotalMax, frekuensiJumlahTertinggi -> frekTotalMax, jumlahTertinggi -> totalMaksimum
    hasilFormat += "Jumlah Terendah: " + angkaTotalMin + " * " + petaFrekuensi.get(angkaTotalMin) + " = " + totalMinimum + "\n"; // output -> hasilFormat, nilaiJumlahTerendah -> angkaTotalMin, hashMapCounter -> petaFrekuensi, nilaiJumlahTerendah -> angkaTotalMin, jumlahTerendah -> totalMinimum
    
    hasilFormat = hasilFormat.replaceAll("\n", "<br/>").trim(); // output -> hasilFormat
    return hasilFormat; // output -> hasilFormat
    }
    
    

    // Helper 
    public static String decode(String base64) {
        return new String(Base64.getDecoder().decode(base64));
    }
}