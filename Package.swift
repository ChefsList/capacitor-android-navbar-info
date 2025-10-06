// swift-tools-version: 5.9
import PackageDescription

let package = Package(
    name: "SystemBars",
    platforms: [.iOS(.v14)],
    products: [
        .library(
            name: "SystemBars",
            targets: ["SystemBarsPlugin"])
    ],
    dependencies: [
        .package(url: "https://github.com/ionic-team/capacitor-swift-pm.git", from: "7.0.0")
    ],
    targets: [
        .target(
            name: "SystemBarsPlugin",
            dependencies: [
                .product(name: "Capacitor", package: "capacitor-swift-pm"),
                .product(name: "Cordova", package: "capacitor-swift-pm")
            ],
            path: "ios/Sources/SystemBarsPlugin"),
        .testTarget(
            name: "SystemBarsPluginTests",
            dependencies: ["SystemBarsPlugin"],
            path: "ios/Tests/SystemBarsPluginTests")
    ]
)