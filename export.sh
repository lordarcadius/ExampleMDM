oldPath=$PWD
cd app/build/outputs/apk/release/
apksigner sign --ks $oldPath/app/keys/ReleaseKey.jks  --ks-pass pass:"primebook123" --ks-key-alias ReleaseKey --key-pass pass:"primebook123" --out ExampleMDM.apk app-release-unsigned.apk
