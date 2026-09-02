package com.example.virgoyt.data.game

import com.example.virgoyt.data.model.Game3DModel
import com.example.virgoyt.data.model.GameEngineType
import com.example.virgoyt.data.model.GameProject
import com.example.virgoyt.data.model.GoogleEarthGISMap
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

class GameStudioEngine {

    private val _projects = MutableStateFlow<List<GameProject>>(
        listOf(
            GameProject(
                title = "Cybernetic Neon Metropolis 3D",
                engineType = GameEngineType.THREE_JS,
                description = "High-poly cyberpunk procedural city with volumetric bloom and raytracing shaders",
                models = listOf(
                    Game3DModel(name = "CyberTower_Alpha", type = "GLTF", vertexCount = 45000, polyCount = 22000),
                    Game3DModel(name = "HoverVehicle_Mk4", type = "FBX", vertexCount = 12000, polyCount = 6500)
                ),
                earthGISMap = GoogleEarthGISMap(
                    locationName = "Tokyo Neo-Shinjuku",
                    latitude = 35.6895,
                    longitude = 139.6917,
                    altitudeMeters = 350.0,
                    terrainResolution = "1m LiDAR Mesh",
                    vegetationDensity = 0.25f,
                    proceduralSeed = 8849102L
                ),
                currentSceneCode = """import * as THREE from 'three';
const scene = new THREE.Scene();
const camera = new THREE.PerspectiveCamera(75, window.innerWidth / window.innerHeight, 0.1, 1000);
const renderer = new THREE.WebGLRenderer({ antialias: true });
renderer.setSize(window.innerWidth, window.innerHeight);
document.body.appendChild(renderer.domElement);

const geometry = new THREE.BoxGeometry(1, 1, 1);
const material = new THREE.MeshStandardMaterial({ color: 0x00f3ff, roughness: 0.2, metalness: 0.8 });
const cube = new THREE.Mesh(geometry, material);
scene.add(cube);

const light = new THREE.PointLight(0xff00ff, 2, 50);
light.position.set(5, 5, 5);
scene.add(light);
"""
            )
        )
    )
    val projects: StateFlow<List<GameProject>> = _projects.asStateFlow()

    private val _selectedProjectId = MutableStateFlow(_projects.value.first().id)
    val selectedProjectId: StateFlow<String> = _selectedProjectId.asStateFlow()

    fun selectProject(id: String) {
        _selectedProjectId.value = id
    }

    fun createProject(title: String, engineType: GameEngineType): GameProject {
        val p = GameProject(title = title, engineType = engineType)
        _projects.value = _projects.value + p
        _selectedProjectId.value = p.id
        return p
    }

    fun add3DModel(projectId: String, model: Game3DModel) {
        _projects.value = _projects.value.map {
            if (it.id == projectId) it.copy(models = it.models + model) else it
        }
    }
}
