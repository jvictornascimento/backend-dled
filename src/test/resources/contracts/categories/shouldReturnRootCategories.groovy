package contracts.categories

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "should return the root category list"
    request {
        method GET()
        url "/v1/categories/root"
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
                        id      : 1,
                        name    : "Drivers",
                        children: []
                ]
        ])
    }
}
