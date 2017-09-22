package utils.geode.server.cassandra.impl;

import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.security.auth.x500.X500Principal;

public class TrustCertificates {

	// supports only one-way or two-way SSL
	public static void trustServerCertificates(String protocol, CassandraProperties config) throws Exception {
		SSLContext sc = javax.net.ssl.SSLContext.getInstance(protocol);
		if (config.getCassandraClientKeystore() != null && config.getCassandraClientKeystore().length() > 0
				&& config.getCassandraClientKeystorePass() != null
				&& config.getCassandraClientKeystorePass().length() > 0) {
			KeyStore ks = KeyStore.getInstance("JKS");
			InputStream is = new FileInputStream(config.getCassandraClientKeystore());
			ks.load(is, config.getCassandraClientKeystorePass().toCharArray());
			KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
			kmf.init(ks, config.getCassandraClientKeystorePass().toCharArray());
			sc.init(kmf.getKeyManagers(), new TrustManager[] { new ManageTrust(config.getCassandraSSLSignerName()) },
					new SecureRandom());
		} else {
			sc.init(new KeyManager[0], new TrustManager[] { new ManageTrust(config.getCassandraSSLSignerName()) },
					new SecureRandom());
		}
		SSLContext.setDefault(sc);
	}

	public static class ManageTrust implements javax.net.ssl.TrustManager, javax.net.ssl.X509TrustManager {
		private String distinguishedSignerName;

		public ManageTrust(String signerName) {
			this.distinguishedSignerName = signerName;
		}

		public java.security.cert.X509Certificate[] getAcceptedIssuers() {
			return null;
		}

		public boolean isServerTrusted(java.security.cert.X509Certificate[] certs) {
			return true;
		}

		public boolean isClientTrusted(java.security.cert.X509Certificate[] certs) {
			return true;
		}

		public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType)
				throws CertificateException {
			for (X509Certificate cert : certs) {
				try {
					cert.checkValidity();
					X500Principal issuer = cert.getIssuerX500Principal();
					if (!distinguishedSignerName.equalsIgnoreCase(issuer.getName())) {
						throw new CertificateException(
								"Certificate signer name does not match cassandraSSLSignerName defined in properties. Certificate signer name: "
										+ issuer.getName());
					}
				} catch (CertificateExpiredException | CertificateNotYetValidException e) {
					throw new CertificateException(e);
				}
			}
			return;
		}

		public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType)
				throws CertificateException {
			return;
		}
	}

}
