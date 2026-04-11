package contracts.products

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "should return the active product list"
    request {
        method GET()
        url "/v1/products"
        headers {
            header("X-API-Key", "test-api-key")
        }
    }
    response {
        status OK()
        headers {
            contentType(applicationJson())
        }
        body([
                [
                        id         : 1,
                        name       : "Driver 24W",
                        price      : 199.9,
                        iconUrl    : "https://cloudinary.test/products/icon.png",
                        codigoRusso: 100,
                        codigoMali : 200,
                        categories : [
                                [
                                        id  : 1,
                                        name: "Drivers"
                                ]
                        ]
                ]
        ])
    }
}
