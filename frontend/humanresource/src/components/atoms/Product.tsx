import 'bootstrap/dist/css/bootstrap.min.css';
import './Product.css';
import {useEffect, useRef, useState} from "react";


function Product(){

    const titleRef = useRef(null);
    const [titleVisible, setProfileVisible] = useState(false)

    useEffect(() => {
        const oberver3 = new IntersectionObserver(
            ([entry]) => {
                if (entry.isIntersecting) {
                    setProfileVisible(true);
                }
            },
            { threshold : 0.3}
        );

        if (titleRef.current) oberver3.observe(titleRef.current);

        return () => {
          oberver3.disconnect();
        };

    }, []);

    return(
        <div className="product-container">
            <div
                ref={titleRef}
                className={`titleheader ${titleVisible ? 'show' : ''}`}
            >
                <h1>Products</h1>
                <h4>Pricing based on your number of employees,</h4>
                <h4>use as much as you want, pay as much as you use</h4>

            </div>
            <div className="productlist">
                <div className="product">
                    <div className="product-header">
                        <img className="small-image"   src="/img/employee.png"/>
                        <h4 className="product-name-style">
                            Employee Management
                        </h4>
                    </div>
                    <div className="product-body">
                        Manage all employee information in a single application
                    </div>
                    <div className="product-btn-bar">
                        <button className="product-manage-btn">
                            Manage
                        </button>
                    </div>

                </div>
                <div className="product">
                    <div className="product-header">
                        <img className="small-image"   src="/img/expens.png"/>
                        <h4 className="product-name-style">
                            Salary Management
                        </h4>
                    </div>
                    <div className="product-body">
                        Update, manage, and report salaries
                    </div>
                    <div className="product-btn-bar">
                        <button className="product-manage-btn">
                            Manage
                        </button>
                    </div>
                </div>
                <div className="product">
                    <div className="product-header">
                        <img className="small-image"   src="/img/shift.png"/>
                        <h4 className="product-name-style">
                            Shift Management
                        </h4>
                    </div>
                    <div className="product-body">
                        Plan and manage your working time in the most efficient way
                    </div>
                    <div className="product-btn-bar">
                        <button className="product-manage-btn">
                            Manage
                        </button>
                    </div>
                </div>
                <div className="product">
                    <div className="product-header">
                        <img className="small-image"   src="/img/assets.png"/>
                        <h4 className="product-name-style">
                            Asset Management
                        </h4>
                    </div>
                    <div className="product-body">
                        Track and manage company assets assigned to your employees
                    </div>
                    <div className="product-btn-bar">
                        <button className="product-manage-btn">
                            Manage
                        </button>
                    </div>
                </div>
            </div>


        </div>

    )

}
export default Product