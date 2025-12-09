// Fragment Shader for CloudPaper Live Wallpaper
// GPU-based cloud generation using FastNoiseLite

precision mediump float;

varying vec2 vTexCoord;

// Uniforms for animation
uniform float uTime;           // Z-position in noise (evolution)
uniform float uDriftX;         // X-offset for wind drift
uniform float uDriftY;         // Y-offset for wind drift
uniform vec2 uResolution;      // Screen resolution

// Cloud appearance parameters
uniform float uFrequency;      // Noise frequency (default: 0.005)
uniform float uThreshold;      // Cloud density threshold (default: 0.5)
uniform vec3 uSkyColor;        // Sky background color
uniform vec3 uCloudColor;      // Cloud color

// FastNoiseLite constants and functions
// We'll use OpenSimplex2S noise with FBM fractal

#define FNL_NOISE_OPENSIMPLEX2S 1
#define FNL_FRACTAL_FBM 1

// Simplified FastNoiseLite state structure for our use case
struct fnl_state {
    int seed;
    float frequency;
    int noise_type;
    int fractal_type;
    int octaves;
    float lacunarity;
    float gain;
};

// Hash functions for noise generation
int fnlHash(int seed, int xPrimed, int yPrimed, int zPrimed) {
    int hash = seed;
    hash ^= xPrimed;
    hash ^= yPrimed;
    hash ^= zPrimed;

    hash = hash * 0x27d4eb2d;
    return hash;
}

float fnlValCoord(int seed, int xPrimed, int yPrimed, int zPrimed) {
    int hash = fnlHash(seed, xPrimed, yPrimed, zPrimed);
    hash *= hash;
    hash ^= hash << 19;
    return float(hash) * (1.0 / 2147483648.0);
}

// Gradient calculation for 3D
float fnlGradCoord(int seed, int xPrimed, int yPrimed, int zPrimed, float xd, float yd, float zd) {
    int hash = fnlHash(seed, xPrimed, yPrimed, zPrimed);
    hash ^= hash >> 15;
    hash &= 63 << 2;

    // Simplified gradient - using hash to select gradient direction
    float gx = (float(hash & 15) - 7.5) / 7.5;
    float gy = (float((hash >> 4) & 15) - 7.5) / 7.5;
    float gz = (float((hash >> 8) & 15) - 7.5) / 7.5;

    return xd * gx + yd * gy + zd * gz;
}

// OpenSimplex2S noise function (simplified 3D implementation)
float fnlSingleOpenSimplex2S(int seed, float x, float y, float z) {
    const float SQRT3 = 1.7320508075688772935;
    const float F3 = 1.0 / 3.0;
    const float G3 = 1.0 / 6.0;

    // Skew the input space
    float s = (x + y + z) * F3;
    int i = int(floor(x + s));
    int j = int(floor(y + s));
    int k = int(floor(z + s));

    float t = float(i + j + k) * G3;
    float X0 = float(i) - t;
    float Y0 = float(j) - t;
    float Z0 = float(k) - t;

    float x0 = x - X0;
    float y0 = y - Y0;
    float z0 = z - Z0;

    // Determine which simplex we're in
    int i1, j1, k1;
    int i2, j2, k2;

    if (x0 >= y0) {
        if (y0 >= z0) {
            i1 = 1; j1 = 0; k1 = 0; i2 = 1; j2 = 1; k2 = 0;
        } else if (x0 >= z0) {
            i1 = 1; j1 = 0; k1 = 0; i2 = 1; j2 = 0; k2 = 1;
        } else {
            i1 = 0; j1 = 0; k1 = 1; i2 = 1; j2 = 0; k2 = 1;
        }
    } else {
        if (y0 < z0) {
            i1 = 0; j1 = 0; k1 = 1; i2 = 0; j2 = 1; k2 = 1;
        } else if (x0 < z0) {
            i1 = 0; j1 = 1; k1 = 0; i2 = 0; j2 = 1; k2 = 1;
        } else {
            i1 = 0; j1 = 1; k1 = 0; i2 = 1; j2 = 1; k2 = 0;
        }
    }

    // Offsets for corners
    float x1 = x0 - float(i1) + G3;
    float y1 = y0 - float(j1) + G3;
    float z1 = z0 - float(k1) + G3;
    float x2 = x0 - float(i2) + 2.0 * G3;
    float y2 = y0 - float(j2) + 2.0 * G3;
    float z2 = z0 - float(k2) + 2.0 * G3;
    float x3 = x0 - 1.0 + 3.0 * G3;
    float y3 = y0 - 1.0 + 3.0 * G3;
    float z3 = z0 - 1.0 + 3.0 * G3;

    // Calculate contribution from four corners
    float n0, n1, n2, n3;

    float t0 = 0.6 - x0 * x0 - y0 * y0 - z0 * z0;
    if (t0 < 0.0) {
        n0 = 0.0;
    } else {
        t0 *= t0;
        n0 = t0 * t0 * fnlGradCoord(seed, i, j, k, x0, y0, z0);
    }

    float t1 = 0.6 - x1 * x1 - y1 * y1 - z1 * z1;
    if (t1 < 0.0) {
        n1 = 0.0;
    } else {
        t1 *= t1;
        n1 = t1 * t1 * fnlGradCoord(seed, i + i1, j + j1, k + k1, x1, y1, z1);
    }

    float t2 = 0.6 - x2 * x2 - y2 * y2 - z2 * z2;
    if (t2 < 0.0) {
        n2 = 0.0;
    } else {
        t2 *= t2;
        n2 = t2 * t2 * fnlGradCoord(seed, i + i2, j + j2, k + k2, x2, y2, z2);
    }

    float t3 = 0.6 - x3 * x3 - y3 * y3 - z3 * z3;
    if (t3 < 0.0) {
        n3 = 0.0;
    } else {
        t3 *= t3;
        n3 = t3 * t3 * fnlGradCoord(seed, i + 1, j + 1, k + 1, x3, y3, z3);
    }

    return (n0 + n1 + n2 + n3) * 32.0;
}

// FBM (Fractal Brownian Motion) implementation
float fnlGenNoiseFBM(fnl_state state, float x, float y, float z) {
    float sum = 0.0;
    float amp = 1.0;

    for (int i = 0; i < 3; i++) { // 3 octaves as per the original implementation
        float noise = fnlSingleOpenSimplex2S(state.seed, x, y, z);
        sum += noise * amp;

        amp *= state.gain;
        x *= state.lacunarity;
        y *= state.lacunarity;
        z *= state.lacunarity;
    }

    return sum;
}

void main() {
    // Calculate world position with drift offset
    vec2 worldPos = vTexCoord * uResolution;
    worldPos.x += uDriftX;
    worldPos.y += uDriftY;

    // Initialize noise state
    fnl_state state;
    state.seed = 1337;
    state.frequency = uFrequency;
    state.noise_type = FNL_NOISE_OPENSIMPLEX2S;
    state.fractal_type = FNL_FRACTAL_FBM;
    state.octaves = 3;
    state.lacunarity = 2.0;
    state.gain = 0.5;

    // Generate noise value using world position and time
    float noiseValue = fnlGenNoiseFBM(
        state,
        worldPos.x * state.frequency,
        worldPos.y * state.frequency,
        uTime
    );

    // Normalize noise value from [-1, 1] to [0, 1]
    noiseValue = (noiseValue + 1.0) * 0.5;

    // Apply threshold for cloud density
    float cloudDensity = smoothstep(uThreshold - 0.1, uThreshold + 0.1, noiseValue);

    // Blend between sky and clouds based on density
    vec3 color = mix(uSkyColor, uCloudColor, cloudDensity);

    // Output final color
    gl_FragColor = vec4(color, 1.0);
}
