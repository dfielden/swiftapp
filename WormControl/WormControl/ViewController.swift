//
//  ViewController.swift
//  WormControl
//
//  Created by Daniel on 14/09/2021.
//

import UIKit

class ViewController: UIViewController {

    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view.
        
        let button = UIButton()
        button.translatesAutoresizingMaskIntoConstraints = false
        view.addSubview(button)
        button.layer.cornerRadius = 12
        button.backgroundColor = UIColor.init(red: 40/255, green: 155/255, blue: 10/255, alpha: 1)
        button.setTitle("Activate 🐛", for: .normal)
        
        let constraints = [
            button.bottomAnchor.constraint(equalTo: view.bottomAnchor, constant: -80),
            button.centerXAnchor.constraint(equalTo: view.centerXAnchor),
            button.widthAnchor.constraint(equalToConstant: 200),
            button.heightAnchor.constraint(equalToConstant: 60),
        ]
        
        NSLayoutConstraint.activate(constraints)
        button.addTarget(self, action: #selector(self.showWormButton(sender:)), for: .touchUpInside)
        
    }
    
    @objc fileprivate func showWormButton(sender:UIButton) {
        print("worm button pushed")
        self.animateView(sender)
        httpPost()
    }
    
    @objc fileprivate func animateView(_ viewToAnimate:UIView) {
        UIView.animate(withDuration: 0.15, delay: 0, usingSpringWithDamping: 0.2, initialSpringVelocity: 0.5, options: .curveEaseIn, animations: {
            viewToAnimate.transform = CGAffineTransform(scaleX: 0.92, y: 0.92)
        
        }) { (_) in
            UIView.animate(withDuration: 0.15, delay: 0, usingSpringWithDamping: 0.4, initialSpringVelocity: 2, options: .curveEaseIn, animations: {
                viewToAnimate.transform = CGAffineTransform(scaleX: 1, y: 1)
            print("animation completed")
        }, completion: nil)
    }
    
    }
    

    func httpPost() {
        // prepare json data
        let json: [String: Any] = ["code": "1234"]

        let jsonData = try? JSONSerialization.data(withJSONObject: json)

        // create post request
        let url = URL(string: "http://10.0.1.130:8080/activateworm")!
        var request = URLRequest(url: url)
        request.httpMethod = "POST"

        // insert json data to the request
        request.httpBody = jsonData

        let task = URLSession.shared.dataTask(with: request) { data, response, error in
            guard let data = data, error == nil else {
                print(error?.localizedDescription ?? "No data")
                return
            }
            let responseJSON = try? JSONSerialization.jsonObject(with: data, options: [])
            if let responseJSON = responseJSON as? [String: Any] {
                print(responseJSON)
            }
        }

        task.resume()
    }


}

