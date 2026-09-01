package com.example.manus.data.cloud

import com.example.manus.data.model.CloudStorageBucket
import com.example.manus.data.model.CloudStorageObject
import com.example.manus.data.model.StorageProviderType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

class CloudStorageEngine {
    private val _buckets = MutableStateFlow<List<CloudStorageBucket>>(
        listOf(
            CloudStorageBucket(
                name = "virgoyt-ue5-assets-prod",
                provider = StorageProviderType.S3_COMPATIBLE,
                region = "us-east-1",
                objectCount = 18,
                totalSizeBytes = 1240000000L,
                objects = listOf(
                    CloudStorageObject("models/dinosaur_t_rex.glb", 45200000L, "model/gltf-binary"),
                    CloudStorageObject("textures/nanite_rock_4k_pbr.tar.gz", 128000000L, "application/gzip"),
                    CloudStorageObject("cinematics/intro_trailer_4k.mp4", 512000000L, "video/mp4"),
                    CloudStorageObject("heightmaps/grand_canyon_dem_05m.tif", 210000000L, "image/tiff")
                )
            ),
            CloudStorageBucket(
                name = "virgoyt-vector-embeddings-gcs",
                provider = StorageProviderType.GOOGLE_CLOUD_STORAGE,
                region = "us-central1",
                objectCount = 8,
                totalSizeBytes = 68000000L,
                objects = listOf(
                    CloudStorageObject("vectors/1536d_hive_index.bin", 34000000L, "application/octet-stream"),
                    CloudStorageObject("metadata/doc_embeddings_v2.json", 18500000L, "application/json"),
                    CloudStorageObject("snapshots/user_pref_vectors_latest.bin", 15500000L, "application/octet-stream")
                )
            ),
            CloudStorageBucket(
                name = "virgoyt-app-deployments-r2",
                provider = StorageProviderType.S3_COMPATIBLE,
                region = "global-edge",
                objectCount = 42,
                totalSizeBytes = 185000000L,
                objects = listOf(
                    CloudStorageObject("web/nextjs_bundle_v1.0.tar.gz", 54000000L, "application/gzip"),
                    CloudStorageObject("android/virgoyt-ai-release.apk", 62000000L, "application/vnd.android.package-archive"),
                    CloudStorageObject("backend/fastapi_docker_image.tar", 69000000L, "application/x-tar")
                )
            )
        )
    )
    val buckets: StateFlow<List<CloudStorageBucket>> = _buckets.asStateFlow()

    private val _selectedBucket = MutableStateFlow<CloudStorageBucket?>(_buckets.value.firstOrNull())
    val selectedBucket: StateFlow<CloudStorageBucket?> = _selectedBucket.asStateFlow()

    fun selectBucket(bucket: CloudStorageBucket) {
        _selectedBucket.value = bucket
    }

    fun uploadObject(bucketName: String, key: String, sizeBytes: Long, mimeType: String) {
        val newObj = CloudStorageObject(
            key = key,
            sizeBytes = sizeBytes,
            mimeType = mimeType,
            lastModified = System.currentTimeMillis()
        )
        _buckets.value = _buckets.value.map { b ->
            if (b.name == bucketName) {
                val updatedObjs = listOf(newObj) + b.objects
                b.copy(
                    objects = updatedObjs,
                    objectCount = updatedObjs.size,
                    totalSizeBytes = b.totalSizeBytes + sizeBytes
                )
            } else b
        }
        if (_selectedBucket.value?.name == bucketName) {
            _selectedBucket.value = _buckets.value.find { it.name == bucketName }
        }
    }

    fun deleteObject(bucketName: String, key: String) {
        _buckets.value = _buckets.value.map { b ->
            if (b.name == bucketName) {
                val updatedObjs = b.objects.filterNot { it.key == key }
                b.copy(
                    objects = updatedObjs,
                    objectCount = updatedObjs.size
                )
            } else b
        }
        if (_selectedBucket.value?.name == bucketName) {
            _selectedBucket.value = _buckets.value.find { it.name == bucketName }
        }
    }
}
