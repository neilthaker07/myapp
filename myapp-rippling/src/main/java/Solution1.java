import java.io.StringReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;
import org.xml.sax.InputSource;

/**
 * Practice: CSV + XML loading → Map, then process query requests against it.
 *
 * Problem: Product Catalog Query Engine
 *   - Load product catalog from CSV  → Map<String, Product>
 *   - Load discount rules from XML   → Map<String, BigDecimal>
 *   - Process QueryRequest objects   → filtered + aggregated results
 *
 * Compile & run (no build tool needed):
 *   javac Solution1.java && java Solution1
 */
public class Solution1 {

    // ── Models ─────────────────────────────────────────────────────────────────

    static class Product {
        final String id, name, category;
        final BigDecimal price;
        final int stock;

        Product(String id, String name, String category, BigDecimal price, int stock) {
            this.id = id; this.name = name; this.category = category;
            this.price = price; this.stock = stock;
        }

        // CSV row: id,name,category,price,stock
        static Product fromCsv(String line) {
            String[] c = line.split(",");
            return new Product(c[0].trim(), c[1].trim(), c[2].trim(),
                    new BigDecimal(c[3].trim()), Integer.parseInt(c[4].trim()));
        }

        @Override
        public String toString() {
            return String.format("Product{id=%-4s name=%-22s category=%-12s price=$%-8s stock=%d}",
                    id, name, category, price.toPlainString(), stock);
        }
    }

    // Input request object — the "JSON / object" the caller passes in
    static class QueryRequest {
        final String category;       // null = any category
        final BigDecimal maxPrice;   // null = no price ceiling
        final boolean inStockOnly;

        QueryRequest(String category, BigDecimal maxPrice, boolean inStockOnly) {
            this.category = category;
            this.maxPrice = maxPrice;
            this.inStockOnly = inStockOnly;
        }
    }

    // ── Data loading ───────────────────────────────────────────────────────────

    // Pattern 1: CSV → Map
    // In CoderPad with a real file: replace the String parameter with
    //   Files.lines(Path.of("products.csv")).collect(Collectors.joining("\n"))
    static Map<String, Product> loadProductsFromCsv(String csv) {
        return Arrays.stream(csv.strip().split("\n"))
                .skip(1)                    // header row
                .map(String::trim)
                .filter(line -> !line.isEmpty())
                .map(Product::fromCsv)
                .collect(Collectors.toMap(
                        p -> p.id,
                        p -> p,
                        (a, b) -> a,        // keep first on duplicate key
                        LinkedHashMap::new  // preserve insertion order
                ));
    }

    // Pattern 2: XML → Map
    // XML shape: <discounts><rule category="headphones" percent="10"/></discounts>
    static Map<String, BigDecimal> loadDiscountsFromXml(String xml) throws Exception {
        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doc = db.parse(new InputSource(new StringReader(xml)));

        NodeList rules = doc.getElementsByTagName("rule");
        Map<String, BigDecimal> discounts = new LinkedHashMap<>();
        for (int i = 0; i < rules.getLength(); i++) {
            Element el = (Element) rules.item(i);
            discounts.put(
                    el.getAttribute("category"),
                    new BigDecimal(el.getAttribute("percent"))
            );
        }
        return discounts;
    }

    // ── Processing logic ───────────────────────────────────────────────────────

    // Query: filter catalog by request fields, sort by price ascending
    static List<Product> query(Map<String, Product> catalog, QueryRequest req) {
        return catalog.values().stream()
                .filter(p -> req.category == null || p.category.equalsIgnoreCase(req.category))
                .filter(p -> req.maxPrice == null || p.price.compareTo(req.maxPrice) <= 0)
                .filter(p -> !req.inStockOnly || p.stock > 0)
                .sorted(Comparator.comparing(p -> p.price))
                .toList();
    }

    // Aggregation: total inventory value (price × stock) per category
    static Map<String, BigDecimal> inventoryValueByCategory(Map<String, Product> catalog) {
        return catalog.values().stream()
                .collect(Collectors.groupingBy(
                        p -> p.category,
                        Collectors.reducing(BigDecimal.ZERO,
                                p -> p.price.multiply(BigDecimal.valueOf(p.stock)),
                                BigDecimal::add)
                ));
    }

    // Transform: apply per-category discount to a product list
    static List<String> applyDiscounts(List<Product> products, Map<String, BigDecimal> discounts) {
        return products.stream()
                .map(p -> {
                    BigDecimal pct = discounts.getOrDefault(p.category, BigDecimal.ZERO);
                    BigDecimal multiplier = BigDecimal.ONE
                            .subtract(pct.divide(BigDecimal.valueOf(100)));
                    BigDecimal salePrice = p.price.multiply(multiplier)
                            .setScale(2, RoundingMode.HALF_UP);
                    return String.format("%-22s  $%-8s → $%-8s  (%.0f%% off)",
                            p.name, p.price.toPlainString(), salePrice.toPlainString(), pct.doubleValue());
                })
                .toList();
    }

    // ── Inline test data (swap for Files.lines() in a real environment) ────────

    static final String PRODUCTS_CSV = """
            id,name,category,price,stock
            P001,Sony WH-1000XM5,headphones,349.99,15
            P002,Apple AirPods Pro,headphones,249.00,8
            P003,Bose QuietComfort,headphones,279.00,0
            P004,Jabra Evolve2,headphones,399.00,5
            P005,Samsung Galaxy Buds,earbuds,149.99,20
            P006,Anker Soundcore,earbuds,49.99,50
            P007,Beats Studio Pro,headphones,349.99,3
            """;

    static final String DISCOUNTS_XML = """
            <discounts>
                <rule category="headphones" percent="10"/>
                <rule category="earbuds"    percent="15"/>
            </discounts>
            """;

    // ── Main ───────────────────────────────────────────────────────────────────

    public static void main(String[] args) throws Exception {
        // ── Load phase ──────────────────────────────────────────────────────────
        Map<String, Product> catalog  = loadProductsFromCsv(PRODUCTS_CSV);
        Map<String, BigDecimal> discounts = loadDiscountsFromXml(DISCOUNTS_XML);

        System.out.println("Loaded " + catalog.size() + " products, "
                + discounts.size() + " discount rules\n");

        // ── Query 1: headphones under $300 (any stock) ──────────────────────────
        System.out.println("=== Headphones ≤ $300 ===");
        query(catalog, new QueryRequest("headphones", new BigDecimal("300"), false))
                .forEach(System.out::println);

        // ── Query 2: in-stock earbuds only ──────────────────────────────────────
        System.out.println("\n=== In-stock earbuds ===");
        query(catalog, new QueryRequest("earbuds", null, true))
                .forEach(System.out::println);

        // ── Aggregation: inventory value by category ─────────────────────────────
        System.out.println("\n=== Inventory value by category ===");
        inventoryValueByCategory(catalog).forEach((cat, total) ->
                System.out.printf("  %-12s  $%s%n", cat, total.setScale(2, RoundingMode.HALF_UP)));

        // ── Transform: discount prices for all in-stock items ───────────────────
        System.out.println("\n=== Sale prices (in-stock only) ===");
        List<Product> inStock = query(catalog, new QueryRequest(null, null, true));
        applyDiscounts(inStock, discounts).forEach(line -> System.out.println("  " + line));
    }
}
