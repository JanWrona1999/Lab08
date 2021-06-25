package pollub.ism.lab08;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import pollub.ism.lab08.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private ArrayAdapter<CharSequence> adapter;
    private String wybraneWarzywoNazwa = null;
    private Integer wybraneWarzywoIlosc = null;

    public enum OperacjaMagazynowa {SKLADUJ, WYDAJ};

    private BazaMagazynowa bazaDanych;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        adapter = ArrayAdapter.createFromResource(this, R.array.Asortyment, android.R.layout.simple_dropdown_item_1line);
        binding.AssortmentSpinner.setAdapter(adapter);
        bazaDanych = Room.databaseBuilder(getApplicationContext(), BazaMagazynowa.class, BazaMagazynowa.NAZWA_BAZY)
                .allowMainThreadQueries().build();

        if(bazaDanych.pozycjaMagazynowaDAO().size() == 0){
            String[] asortyment = getResources().getStringArray(R.array.Asortyment);
            for(String nazwa : asortyment){
                PozycjaMagazynowa pozycjaMagazynowa = new PozycjaMagazynowa();
                pozycjaMagazynowa.NAME = nazwa; pozycjaMagazynowa.QUANTITY = 0;
                bazaDanych.pozycjaMagazynowaDAO().insert(pozycjaMagazynowa);
            }
        }
        binding.DatabaseAdd.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                zmienStan(OperacjaMagazynowa.SKLADUJ);
            }
        });

        binding.DatabaseRemove.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                zmienStan(OperacjaMagazynowa.WYDAJ);
            }
        });

        binding.AssortmentSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                wybraneWarzywoNazwa = adapter.getItem(i).toString(); // <---
                aktualizuj();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                //Nie będziemy implementować, ale musi być
            }
        });
    }
    private void aktualizuj(){
        wybraneWarzywoIlosc = bazaDanych.pozycjaMagazynowaDAO().findQuantityByName(wybraneWarzywoNazwa);
        binding.CurrentAssortmentState.setText("Stan magazynu dla " + wybraneWarzywoNazwa + " wynosi: " + wybraneWarzywoIlosc);
        StringBuilder updateList = new StringBuilder();
        for (MagazynUpdate result : bazaDanych.MagazynUpdateDAO().findUpdatesByItemName(wybraneWarzywoNazwa)) {
            updateList.append(String.format("%s, %s, %s\n", result.DATE, result.OLD_QUANTITY, result.NEW_QUANTITY));
        }

        binding.QuantityChangelog.setText(updateList.toString());
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void zmienStan(OperacjaMagazynowa operacja){

        Integer zmianaIlosci = null, nowaIlosc = null;

        try {
            zmianaIlosci = Integer.parseInt(binding.AssortmentQuantity.getText().toString());
        }catch(NumberFormatException ex){
            return;
        }finally {
            binding.AssortmentQuantity.setText("");
        }

        int staraIlosc = wybraneWarzywoIlosc;
        switch (operacja){
            case SKLADUJ: nowaIlosc = wybraneWarzywoIlosc + zmianaIlosci; break;
            case WYDAJ: nowaIlosc = wybraneWarzywoIlosc - zmianaIlosci; break;
        }

        bazaDanych.pozycjaMagazynowaDAO().updateQuantityByName(wybraneWarzywoNazwa,nowaIlosc);

        ZonedDateTime updateTime = ZonedDateTime.now(ZoneId.of("UTC+2"));
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm:ss");

        MagazynUpdate aktualizujIlosc = new MagazynUpdate(updateTime.format(timeFormatter), wybraneWarzywoNazwa,staraIlosc, wybraneWarzywoIlosc);
        bazaDanych.MagazynUpdateDAO().insert(aktualizujIlosc);

        aktualizuj();
    }
}