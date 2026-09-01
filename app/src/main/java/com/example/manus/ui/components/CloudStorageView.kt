package com.example.manus.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.InsertDriveFile
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Link
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.manus.data.model.CloudStorageBucket
import com.example.manus.ui.ManusCloudViewModel
import com.example.ui.theme.ManusCyan
import com.example.ui.theme.ManusEmerald
import com.example.ui.theme.ManusIndigo
import com.example.ui.theme.ManusRed
import com.example.ui.theme.ManusSlate300
import com.example.ui.theme.ManusSlate400
import com.example.ui.theme.ManusSlate500
import com.example.ui.theme.ManusSlate800
import com.example.ui.theme.ManusSlate850
import com.example.ui.theme.ManusSlate900
import com.example.ui.theme.ManusSlate950
import com.example.ui.theme.ManusWhite
import com.example.ui.theme.SleekBorder
import com.example.ui.theme.VirgoCyanGlow
import com.example.ui.theme.VirgoGlassCard
import com.example.ui.theme.VirgoNeonViolet

@Composable
fun CloudStorageView(
    viewModel: ManusCloudViewModel,
    modifier: Modifier = Modifier
) {
    val storageEngine = viewModel.cloudStorageEngine
    val buckets by storageEngine.buckets.collectAsState()
    val selectedBucket by storageEngine.selectedBucket.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ManusSlate950)
            .padding(12.dp)
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(VirgoGlassCard)
                .border(1.dp, SleekBorder, RoundedCornerShape(12.dp))
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Cloud,
                    contentDescription = "Cloud Storage",
                    tint = VirgoCyanGlow,
                    modifier = Modifier.size(20.dp)
                )
                Column {
                    Text(
                        text = "Cloud Object Storage & Buckets",
                        color = ManusWhite,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "AWS S3 • Google Cloud Storage • Cloudflare R2",
                        color = ManusSlate400,
                        fontSize = 10.sp
                    )
                }
            }

            Button(
                onClick = {
                    selectedBucket?.let { b ->
                        storageEngine.uploadObject(
                            b.name,
                            "models/uploaded_asset_${(100..999).random()}.glb",
                            28500000L,
                            "model/gltf-binary"
                        )
                        viewModel.showToast("Uploaded asset to ${b.name}")
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = ManusIndigo),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.height(32.dp).testTag("upload_to_bucket_btn")
            ) {
                Icon(imageVector = Icons.Default.CloudUpload, contentDescription = "Upload", tint = ManusWhite, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Upload Object", fontSize = 10.5.sp, color = ManusWhite)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Bucket Selector Horizontal Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            buckets.forEach { bucket ->
                val isSelected = selectedBucket?.name == bucket.name
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .border(
                            1.dp,
                            if (isSelected) VirgoCyanGlow else SleekBorder,
                            RoundedCornerShape(10.dp)
                        )
                        .clickable { storageEngine.selectBucket(bucket) },
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) VirgoGlassCard else ManusSlate900
                    )
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(text = bucket.provider.icon, fontSize = 13.sp)
                            Text(
                                text = bucket.name,
                                color = if (isSelected) VirgoCyanGlow else ManusWhite,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                maxLines = 1
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${bucket.region} • ${bucket.objectCount} objs",
                            color = ManusSlate400,
                            fontSize = 9.5.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Selected Bucket Object Browser
        selectedBucket?.let { bucket ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, SleekBorder, RoundedCornerShape(12.dp)),
                colors = CardDefaults.cardColors(containerColor = ManusSlate900)
            ) {
                Column(modifier = Modifier.fillMaxSize().padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "OBJECTS IN s3://${bucket.name}/",
                            color = VirgoCyanGlow,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "${bucket.objects.size} files (${bucket.totalSizeBytes / 1024 / 1024} MB)",
                            color = ManusSlate400,
                            fontSize = 10.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    LazyColumn(
                        modifier = Modifier.fillMaxWidth().weight(1f),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(bucket.objects) { obj ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(ManusSlate850)
                                    .border(1.dp, SleekBorder, RoundedCornerShape(8.dp))
                                    .padding(horizontal = 10.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.InsertDriveFile,
                                        contentDescription = "File",
                                        tint = VirgoCyanGlow,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Column {
                                        Text(
                                            text = obj.key,
                                            color = ManusWhite,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            fontFamily = FontFamily.Monospace
                                        )
                                        Text(
                                            text = "${obj.mimeType} • ${(obj.sizeBytes / 1024 / 1024.0).let { "%.2f".format(it) }} MB • ETag: ${obj.etag}",
                                            color = ManusSlate400,
                                            fontSize = 9.5.sp,
                                            fontFamily = FontFamily.Monospace
                                        )
                                    }
                                }

                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    IconButton(
                                        onClick = {
                                            viewModel.showToast("Signed URL generated for ${obj.key}")
                                        },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Link,
                                            contentDescription = "Copy Link",
                                            tint = ManusSlate300,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                    IconButton(
                                        onClick = {
                                            storageEngine.deleteObject(bucket.name, obj.key)
                                            viewModel.showToast("Deleted ${obj.key}")
                                        },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Delete",
                                            tint = ManusRed,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
