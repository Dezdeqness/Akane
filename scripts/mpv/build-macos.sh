#!/usr/bin/env bash
set -euo pipefail

HERE="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$(cd "${HERE}/../.." && pwd)"
WORK="${HERE}/.work-macos"

if [ "$(uname -m)" != "arm64" ]; then
  echo "This kit targets Apple Silicon only; got arch $(uname -m)." >&2
  exit 1
fi
PREFIX="darwin-aarch64"
OUT="${REPO_ROOT}/features/videoplayer/src/desktopMain/resources/mpv/${PREFIX}"
mkdir -p "${OUT}"

# 1. Build-time deps. meson/ninja/nasm are the toolchain; the libass/freetype/
#    harfbuzz/fribidi/libplacebo/lcms2/uchardet dylibs get folded into the final
#    artifact by dylibbundler below.
brew install meson ninja nasm pkg-config \
  libass freetype harfbuzz fribidi little-cms2 uchardet dylibbundler || true

# 2. mpv-build with our trimmed options.
if [ ! -d "${WORK}/mpv-build" ]; then
  git clone https://github.com/mpv-player/mpv-build.git "${WORK}/mpv-build"
fi
cd "${WORK}/mpv-build"
cp "${HERE}/ffmpeg_options" ffmpeg_options
cp "${HERE}/mpv_options"    mpv_options
./update
./rebuild -j"$(sysctl -n hw.ncpu)"

# 3. Copy the built libmpv into resources and fold every dependency next to it,
#    rewriting install names to @loader_path/ so the whole folder is relocatable.
BUILT="$(find "${WORK}/mpv-build/mpv/build" -name 'libmpv.*.dylib' | head -1)"
[ -n "${BUILT}" ] || { echo "libmpv build output not found" >&2; exit 1; }
cp "${BUILT}" "${OUT}/libmpv.2.dylib"
dylibbundler -of -b -x "${OUT}/libmpv.2.dylib" -d "${OUT}" -p '@loader_path/'
strip -x -S "${OUT}/libmpv.2.dylib" || true

echo
echo "OK -> ${OUT}/libmpv.2.dylib (+ dependency dylibs in the same folder)"
echo "Verify it is self-contained:"
echo "  otool -L '${OUT}/libmpv.2.dylib'   # only /usr/lib, /System and @loader_path entries"
