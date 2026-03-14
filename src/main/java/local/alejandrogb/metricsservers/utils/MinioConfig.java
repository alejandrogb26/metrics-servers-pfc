package local.alejandrogb.metricsservers.utils;

import io.minio.MinioClient;

public class MinioConfig {
	private static MinioClient minioClient;

	public static final String ENDPOINT = "http://10.0.1.170:9000";
	private static final String ACCESS_KEY = "g4Zga7omMnQvdUQwMWc1";
	private static final String SECRET_KEY = "2sNneAqjcCJEqsDU08pvfnhEd2FuSNqS7QSFH4kF";

	public static MinioClient getClient() {
		if (minioClient == null)
			minioClient = MinioClient.builder().endpoint(ENDPOINT).credentials(ACCESS_KEY, SECRET_KEY).build();

		return minioClient;
	}
}
