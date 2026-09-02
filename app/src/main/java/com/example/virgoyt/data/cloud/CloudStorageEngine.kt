package com.example.virgoyt.data.cloud

import com.example.virgoyt.data.model.CloudStorageBucket
import com.example.virgoyt.data.model.CloudStorageObject
import com.example.virgoyt.data.model.StorageProviderType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class CloudStorageEngine {

    private val _buckets = MutableStateFlow<List<CloudStorageBucket>>(
        listOf(
            CloudStorageBucket(name = "virgoyt-artifacts-asia-east1", provider = StorageProviderType.GOOGLE_CLOUD_STORAGE, region = "asia-east1", totalObjects = 14, totalBytes = 24500000L),
            CloudStorageBucket(name = "production-assets-s3", provider = StorageProviderType.AWS_S3, region = "us-east-1", totalObjects = 42, totalBytes = 189000000L),
            CloudStorageBucket(name = "fast-edge-r2", provider = StorageProviderType.CLOUDFLARE_R2, region = "global", totalObjects = 8, totalBytes = 5400000L)
        )
    )
    val buckets: StateFlow<List<CloudStorageBucket>> = _buckets.asStateFlow()

    private val _objects = MutableStateFlow<List<CloudStorageObject>>(
        listOf(
            CloudStorageObject(bucketId = "virgoyt-artifacts-asia-east1", key = "release/v1.0.0/app-release.apk", sizeBytes = 18450000L, contentType = "application/vnd.android.package-archive"),
            CloudStorageObject(bucketId = "virgoyt-artifacts-asia-east1", key = "models/cyber_scene.glb", sizeBytes = 6050000L, contentType = "model/gltf-binary")
        )
    )
    val objects: StateFlow<List<CloudStorageObject>> = _objects.asStateFlow()

    fun createBucket(name: String, provider: StorageProviderType, region: String): CloudStorageBucket {
        val b = CloudStorageBucket(name = name, provider = provider, region = region)
        _buckets.value = _buckets.value + b
        return b
    }

    fun deleteBucket(id: String) {
        _buckets.value = _buckets.value.filterNot { it.id == id }
    }
}
