# Payment Optimizer

Aplikacja do optymalizacji płatności w systemie e-commerce, która maksymalizuje rabaty poprzez inteligentne zarządzanie różnymi metodami płatności.

## Opis projektu

System optymalizuje płatności za zamówienia w supermarkecie internetowym, uwzględniając:
- Płatności tradycyjnymi metodami (karty płatnicze, przelewy bankowe)
- Płatności punktami lojalnościowymi
- Różne rodzaje rabatów i promocji
- Limity dostępnych środków

### Główne funkcjonalności

1. **Metody płatności**:
   - Pełna płatność jedną tradycyjną metodą
   - Pełna płatność punktami lojalnościowymi
   - Płatność mieszana (część punktami, część tradycyjną metodą)

2. **System rabatów**:
   - Rabaty bankowe za pełną płatność kartą
   - Rabat 10% za częściową płatność punktami (min. 10% wartości zamówienia)
   - Rabat za pełną płatność punktami

## Wymagania systemowe

- Java 21
- Maven 3.6.0 lub nowszy

## Instalacja i uruchomienie

1. **Budowanie projektu**:
   ```bash
   mvn clean package
   ```

2. **Uruchomienie aplikacji**:
   ```bash
   java -jar target/payment-optimizer-1.0-SNAPSHOT-jar-with-dependencies.jar <ścieżka_do_orders.json> <ścieżka_do_paymentmethods.json>
   ```

## Format danych wejściowych

### 1. Plik orders.json
```json
[
  {
    "id": "ORDER1",
    "value": "100.00",
    "promotions": ["mZysk", "BosBankrut"]
  }
]
```

Gdzie:
- `id` - identyfikator zamówienia
- `value` - kwota zamówienia (z dokładnością do 2 miejsc po przecinku)
- `promotions` - lista dostępnych promocji (opcjonalne)

### 2. Plik paymentmethods.json
```json
[
  {
    "id": "PUNKTY",
    "discount": "15",
    "limit": "100.00"
  }
]
```

Gdzie:
- `id` - nazwa metody płatności
- `discount` - procentowy rabat
- `limit` - maksymalna dostępna kwota

## Format wyjściowy

Program wypisuje na standardowe wyjście sumaryczne kwoty wydane z podziałem na metody płatności:
```
mZysk 165.00
BosBankrut 190.00
PUNKTY 100.00
```

## Testy

Uruchomienie testów:
```bash
mvn test
```

## Struktura projektu

```
src/
├── main/
│   └── java/
│       └── org/
│           └── example/
│               ├── model/
│               │   ├── Order.java
│               │   ├── PaymentMethod.java
│               │   └── PaymentResult.java
│               ├── service/
│               │   ├── PaymentOptimizer.java
│               │   └── JsonReader.java
│               └── Main.java
└── test/
    └── java/
        └── org/
            └── example/
                └── service/
                    └── PaymentOptimizerTest.java
```

## Algorytm optymalizacji

1. Sortowanie zamówień od największej do najmniejszej wartości
2. Przypisanie najlepszych promocji do zamówień
3. Obsługa pozostałych zamówień:
   - Sprawdzenie możliwości pełnej płatności punktami
   - Sprawdzenie możliwości częściowej płatności punktami
   - Użycie tradycyjnych metod płatności

## Autor

Stanisław Karwala
 