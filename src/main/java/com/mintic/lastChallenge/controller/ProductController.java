package com.mintic.lastChallenge.controller;

import com.mintic.lastChallenge.model.Product;
import com.mintic.lastChallenge.model.ProductRepository;
import com.mintic.lastChallenge.view.StoreGUI;
import com.mintic.lastChallenge.view.UpdateForm;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.util.List;

public class ProductController implements ActionListener {
    ProductRepository repository;
    StoreGUI view;
    UpdateForm updateForm;

    public ProductController(){
        super();
        this.updateTable();
    }

    public ProductController(ProductRepository repository, StoreGUI storeGUI) {
        super();
        this.repository = repository;
        this.view = storeGUI;
        this.updateForm = new UpdateForm();
        this.addEvents();
        this.updateTable();
        this.view.setVisible(true);
    }

    private void addEvents() {
        this.view.getBtnAdd().addActionListener(this);
        this.view.getBtnDelete().addActionListener(this);
        this.view.getBtnUpdate().addActionListener(this);
        this.view.getBtnSummary().addActionListener(this);
        this.updateForm.getBtnUpdate().addActionListener(this);
    }

    private void updateTable() {
        String[][] data = this.obtainDataTable();
        this.view.getjTable1().setModel(new javax.swing.table.DefaultTableModel(
                data,
                new String[]{
                        "ID", "Nombre", "Precio", "Inventario"
                }
        ));
    }

    private String[][] obtainDataTable(){
        List<Product> productList = (List<Product>)this.repository.findAll();
        String[][] data = new String[productList.size()][4];
        for (int i = 0; i < productList.size(); i++) {
            data[i][0] = String.valueOf(productList.get(i).getCode());
            data[i][1] = productList.get(i).getName();
            data[i][2] = String.valueOf(productList.get(i).getPrice());
            data[i][3] = String.valueOf(productList.get(i).getQuantity());
        }
        return data;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == this.view.getBtnAdd()){
            this.addProduct();
        }else if(e.getSource() == this.view.getBtnUpdate()){
            this.displayUpdateForm();
        }else if(e.getSource() == this.view.getBtnDelete()){
            this.deleteProduct();
        }else if(e.getSource() == this.view.getBtnSummary()){
            this.summary();
        }else if(e.getSource() == this.updateForm.getBtnUpdate()){
            this.updateProduct();
        }
    }

    private void summary() {
        JOptionPane.showMessageDialog(null, this.generateReport());
    }

    public String generateReport(){
        String [] summary = this.storeSummary();
        return "Producto precio mayor: "+summary[0]+ "\n"+
                "Producto precio menor: "+summary[1]+ "\n"+
                "Promedio precios: "+summary[2]+ "\n"+
                "Valor del inventario: "+summary[3];
    }

    private String[] storeSummary(){
        String [] rta = new String[4];
        Product cheapestProduct = new Product();
        Product moreExpensiveProduct = new Product();
        double totalStockPrice = 0.0;
        double averagePrice;
        double sumPrices = 0.0;
        int counter = 0;
        for (Product product : this.repository.findAll()) {
            if(cheapestProduct.getName() == null){
                cheapestProduct = product;
            }
            if(moreExpensiveProduct.getName() == null){
                moreExpensiveProduct = product;
            }
            if(product.getPrice() < cheapestProduct.getPrice()){
                cheapestProduct = product;
            }
            if(product.getPrice() > moreExpensiveProduct.getPrice()){
                moreExpensiveProduct = product;
            }
            counter++;
            sumPrices += product.getPrice();
            totalStockPrice += product.getQuantity() * product.getPrice();
        }
        averagePrice = (double)Math.round((sumPrices/counter) * 10) / 10;
        rta[0] = moreExpensiveProduct.getName();
        rta[1] = cheapestProduct.getName();
        rta[2] = String.valueOf(averagePrice);
        rta[3] = String.valueOf(totalStockPrice);
        return rta;
    }

    private void deleteProduct() {
        int actualRow = this.view.getjTable1().getSelectedRow();
        if(actualRow != -1){
            int productId = Integer.parseInt(
                    (String) this.view.getjTable1().getModel().getValueAt(actualRow , 0));
            this.repository.deleteById(productId);
            this.updateTable();
            JOptionPane.showMessageDialog(null, "El producto fue borrado exitosamente");
        }else{
            JOptionPane.showMessageDialog(null, "Seleccione un producto de la tabla",
                    "Warning", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void updateProduct() {
        if(!this.emptyFields("update")){
            String name = this.updateForm.getTxtName().getText();
            Product oldProductData = this.productExistByName(name);
            if(oldProductData != null){
                double value = Double.parseDouble(this.updateForm.getTxtPrice().getText());
                int stock = Integer.parseInt(this.updateForm.getTxtStock().getText());
                oldProductData.setPrice(value);
                oldProductData.setName(name);
                oldProductData.setQuantity(stock);
                this.repository.save(oldProductData);
                this.updateForm.dispatchEvent(new WindowEvent(this.updateForm, WindowEvent.WINDOW_CLOSING));
                JOptionPane.showMessageDialog(null, "El producto fue actualizado exitosamente");
                this.updateTable();
            }else{
                JOptionPane.showMessageDialog(null, "Asegurese de que el producto existe en la base de datos",
                        "Warning", JOptionPane.WARNING_MESSAGE);
            }
        }else{
            JOptionPane.showMessageDialog(null, "Todos los campos son obligatorios",
                    "Warning", JOptionPane.WARNING_MESSAGE);
        }
        this.cleanTextFields("update");
    }


    private void displayUpdateForm(){
        this.updateForm.setVisible(true);
        this.updateForm.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    }

    //it return true if there is some field empty
    private boolean emptyFields(String instruction){
        switch (instruction){
            case ("add"):
                return (this.view.getTxtName().getText().isEmpty()
                        || this.view.getTxtPrice().getText().isEmpty()
                        || this.view.getTxtStock().getText().isEmpty());
            case ("update"):
                return (this.updateForm.getTxtName().getText().isEmpty()
                        || this.updateForm.getTxtPrice().getText().isEmpty()
                        || this.updateForm.getTxtStock().getText().isEmpty());
            default:
                return true;
        }
    }

    /* restore the text values of the TextFields after pressing the
    button update or add*/
    private void cleanTextFields(String frame){
        switch (frame){
            case ("add"):
                this.view.getTxtPrice().setText("");
                this.view.getTxtName().setText("");
                this.view.getTxtStock().setText("");
                break;
            case ("update"):
                this.updateForm.getTxtName().setText("");
                this.updateForm.getTxtPrice().setText("");
                this.updateForm.getTxtStock().setText("");
        }
    }
    /*look in the database for a product with an specific name, if it find it,
    returns the product, if not, returns null*/
    private Product productExistByName(String name) {
        Product repeated = null;
        for (Product value : this.repository.findAll()) {
            if (value.getName().equalsIgnoreCase(name))
                repeated = value;
        }
        return repeated;
    }

    private void addProduct() {
        if(!this.emptyFields("add")){
            String name = this.view.getTxtName().getText();
            if(this.productExistByName(name) == null){
                double price = Double.parseDouble(this.view.getTxtPrice().getText());
                int quantity = Integer.parseInt(this.view.getTxtStock().getText());
                Product product = new Product(name, price, quantity);
                this.repository.save(product);
                JOptionPane.showMessageDialog(null, "El producto fue agregado exitosamente");
                this.updateTable();
            }else{
                JOptionPane.showMessageDialog(null, "El producto ya existe",
                        "Warning", JOptionPane.WARNING_MESSAGE);
            }
        }else{
            JOptionPane.showMessageDialog(null, "Todos los campos son obligatorios",
                    "Warning", JOptionPane.WARNING_MESSAGE);
        }
        this.cleanTextFields("add");
    }

}