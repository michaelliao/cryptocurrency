package com.itranswarp.bitcoin.wallet;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableModel;

import com.itranswarp.bitcoin.util.Secp256k1Utils;

public class Wallet extends JFrame {

	private SecretKeyManager secretKeyManager;

	public Wallet(SecretKeyManager secretKeyManager) {
		this.secretKeyManager = secretKeyManager;
		initComponents();
		updateBalance();
	}

	public void setBalance(long satoshi) {
		if (satoshi < 0) {
			lblBalance.setText("?");
		} else {
			double btc = satoshi / 100000000.0;
			lblBalance.setText(String.format("%.8f BTC", btc));
		}
	}

	void preparePay() {
		System.out.println("prepare transaction...");
		byte[] toAddr;
		double amount;
		double fee;
		try {
			toAddr = Secp256k1Utils.publicKeyAddressToBytes(this.txtPayAddress.getText().trim());
		} catch (Exception e) {
			throw new RuntimeException("Invalid address.");
		}
		try {
			amount = Double.parseDouble(this.txtPayBtc.getText().trim());
			if (amount < 0) {
				throw new NumberFormatException();
			}
		} catch (NumberFormatException e) {
			throw new RuntimeException("Invalid amount.");
		}
		try {
			fee = Double.parseDouble(this.txtPayFee.getText().trim());
			if (fee >= amount || fee < 0) {
				throw new NumberFormatException();
			}
		} catch (NumberFormatException e) {
			throw new RuntimeException("Invalid fee.");
		}
	}

	void prepareAddAddress() {
		System.out.println("prepare add address...");
		String label = JOptionPane.showInputDialog(this, "Label of new bitcoin address:");
		if (label != null) {
			System.out.println("Generate new keypair...");
			this.secretKeyManager.generateNewKey(label);
			updateAddressTable();
		}
	}

	List<String> getAddresses() {
		return this.secretKeyManager.getKeys().stream().map((key) -> {
			return key.toPublicAddress();
		}).collect(Collectors.toList());
	}

	void updateAddressTable() {
		List<Object[]> rows = new ArrayList<>();
		for (SecretKey key : this.secretKeyManager.getKeys()) {
			rows.add(new Object[] { key.label, key.toPublicAddress(), "" });
		}
		DefaultTableModel model = new DefaultTableModel(rows.toArray(new Object[rows.size()][]),
				new String[] { "Label", "Address", "Balance" }) {
			public Class<?> getColumnClass(int columnIndex) {
				return String.class;
			}

			public boolean isCellEditable(int rowIndex, int columnIndex) {
				return false;
			}
		};
		this.tblAddresses.setModel(model);
		updateBalance();
	}

	void updateBalance() {
		setBalance(-1);
		new Thread() {
			public void run() {
				try {
					Thread.sleep(5000L);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						setBalance(4000000L);
					}
				});
			}
		}.start();
	}

	void initComponents() {
		jPanelPay = new JPanel();
		lblToAddress = new JLabel();
		txtPayAddress = new JTextField();
		lblPayBtc = new JLabel();
		txtPayBtc = new JTextField();
		lblPayFee = new JLabel();
		txtPayFee = new JTextField();
		btnPay = new JButton();
		jPanelAddresses = new JPanel();
		btnAddAddress = new JButton();
		jScrollPaneForTable = new JScrollPane();
		tblAddresses = new JTable();
		jPanelBalance = new JPanel();
		lblBalance = new JLabel();
		lblPromptBalance = new JLabel();

		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setTitle("My Wallet (WARNING: TEST ONLY)");

		jPanelPay.setBorder(BorderFactory.createTitledBorder("Pay"));
		lblToAddress.setText("To address:");
		lblPayBtc.setText("Amount:");
		lblPayFee.setText("Fee:");
		txtPayFee.setText("0.001");
		btnPay.setText("Pay");
		btnPay.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				try {
					preparePay();
				} catch (Exception e) {
					JOptionPane.showMessageDialog(null, e.getMessage());
				}
			}
		});
		GroupLayout jPanelPayLayout = new GroupLayout(jPanelPay);
		jPanelPay.setLayout(jPanelPayLayout);
		jPanelPayLayout.setHorizontalGroup(jPanelPayLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(jPanelPayLayout.createSequentialGroup().addContainerGap()
						.addGroup(jPanelPayLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
								.addComponent(lblPayFee).addComponent(lblPayBtc).addComponent(lblToAddress))
						.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
						.addGroup(jPanelPayLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
								.addComponent(txtPayAddress).addComponent(txtPayFee).addComponent(txtPayBtc).addGroup(
										jPanelPayLayout.createSequentialGroup().addComponent(btnPay).addGap(0, 0,
												Short.MAX_VALUE)))
						.addContainerGap()));
		jPanelPayLayout.setVerticalGroup(jPanelPayLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(jPanelPayLayout.createSequentialGroup()
						.addGroup(jPanelPayLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
								.addComponent(lblToAddress).addComponent(txtPayAddress, GroupLayout.PREFERRED_SIZE,
										GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
						.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
						.addGroup(jPanelPayLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
								.addComponent(lblPayBtc).addComponent(txtPayBtc, GroupLayout.PREFERRED_SIZE,
										GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
						.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
						.addGroup(jPanelPayLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
								.addComponent(lblPayFee).addComponent(txtPayFee, GroupLayout.PREFERRED_SIZE,
										GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
						.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE,
								Short.MAX_VALUE)
						.addComponent(btnPay).addContainerGap()));
		jPanelAddresses.setBorder(BorderFactory.createTitledBorder("Bitcoin Addresses"));
		btnAddAddress.setText("Add");
		btnAddAddress.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				prepareAddAddress();
			}
		});
		tblAddresses.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.updateAddressTable();
		jScrollPaneForTable.setViewportView(tblAddresses);
		GroupLayout jPanelAddressesLayout = new GroupLayout(jPanelAddresses);
		jPanelAddresses.setLayout(jPanelAddressesLayout);
		jPanelAddressesLayout.setHorizontalGroup(jPanelAddressesLayout
				.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(jPanelAddressesLayout.createSequentialGroup().addContainerGap()
						.addComponent(jScrollPaneForTable, GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
						.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
						.addGroup(jPanelAddressesLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
								.addComponent(btnAddAddress, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE,
										Short.MAX_VALUE))));
		jPanelAddressesLayout.setVerticalGroup(jPanelAddressesLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(jPanelAddressesLayout.createSequentialGroup().addContainerGap()
						.addGroup(jPanelAddressesLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
								.addGroup(jPanelAddressesLayout.createSequentialGroup().addComponent(btnAddAddress)
										.addGap(0, 156, Short.MAX_VALUE))
								.addComponent(jScrollPaneForTable, GroupLayout.Alignment.TRAILING,
										GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
						.addContainerGap()));

		jPanelBalance.setBorder(BorderFactory.createTitledBorder("Wallet"));
		lblBalance.setText("?");
		lblPromptBalance.setText("Balance:");
		GroupLayout jPanelBalanceLayout = new GroupLayout(jPanelBalance);
		jPanelBalance.setLayout(jPanelBalanceLayout);
		jPanelBalanceLayout.setHorizontalGroup(jPanelBalanceLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(GroupLayout.Alignment.TRAILING, jPanelBalanceLayout.createSequentialGroup().addContainerGap()
						.addComponent(lblPromptBalance, GroupLayout.PREFERRED_SIZE, 63, GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(lblBalance, GroupLayout.PREFERRED_SIZE, 208, GroupLayout.PREFERRED_SIZE)
						.addContainerGap(162, Short.MAX_VALUE)));
		jPanelBalanceLayout.setVerticalGroup(jPanelBalanceLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(jPanelBalanceLayout.createSequentialGroup().addContainerGap()
						.addGroup(jPanelBalanceLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
								.addComponent(lblPromptBalance).addComponent(lblBalance))
						.addGap(0, 15, Short.MAX_VALUE)));

		GroupLayout layout = new GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup().addContainerGap()
						.addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
								.addComponent(jPanelBalance, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE,
										Short.MAX_VALUE)
								.addComponent(jPanelAddresses, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE,
										GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(jPanelPay, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE,
										GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
						.addContainerGap()));
		layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(layout
				.createSequentialGroup().addContainerGap()
				.addComponent(jPanelBalance, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
						GroupLayout.PREFERRED_SIZE)
				.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
				.addComponent(jPanelPay, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
						GroupLayout.PREFERRED_SIZE)
				.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
				.addComponent(jPanelAddresses, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
				.addContainerGap()));
		pack();
	}

	public static void main(String args[]) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				String password = JOptionPane.showInputDialog(null, "Enter wallet password:");
				if (password == null) {
					return;
				}
				char[] pwd = password.toCharArray();
				SecretKeyManager secretKeyManager = null;
				try {
					secretKeyManager = new SecretKeyManager(pwd);
				} catch (RuntimeException e2) {
					JOptionPane.showMessageDialog(null, "Failed load wallet file.");
					return;
				}
				new Wallet(secretKeyManager).setVisible(true);
			}
		});
	}

	// Variables declaration
	private JButton btnAddAddress;
	private JButton btnPay;
	private JPanel jPanelAddresses;
	private JPanel jPanelBalance;
	private JPanel jPanelPay;
	private JScrollPane jScrollPaneForTable;
	private JLabel lblBalance;
	private JLabel lblPayBtc;
	private JLabel lblPayFee;
	private JLabel lblPromptBalance;
	private JLabel lblToAddress;
	private JTable tblAddresses;
	private JTextField txtPayAddress;
	private JTextField txtPayBtc;
	private JTextField txtPayFee;
	// End of variables declaration

}
