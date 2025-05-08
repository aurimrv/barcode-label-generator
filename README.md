# barcode-label-generator

Barcode Label Generator for Products Identification using OKAPI.

It is configure to print information in small labels (width 33mm x height 22mm)

Label information is provided via CSV file. 

CSV columns are:
 - productName
 - productColor
 - barcodeValue

```
mvn clean package
java -jar target/okapi-code93-label-1.0-jar-with-dependencies.jar products/ES2522.csv
```