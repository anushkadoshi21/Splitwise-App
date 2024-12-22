import React, { useState } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';
import './LoginPage.css';

const LoginPage = () => {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');
    const [loading, setLoading] = useState(false);
    const navigate = useNavigate();
    const [isSignUpModalOpen, setIsSignUpModalOpen] = useState(false);
    const [signUpUsername, setSignUpUsername] = useState('');
    const [signUpPassword, setSignUpPassword] = useState('');
    const [showSuccess, setShowSuccess] = useState(false);

    const handleSignUp = async(e) => {
        e.preventDefault();
        setError('');
        const loginData = {
            userName: signUpUsername, // Match the field name exactly
            password: signUpPassword
        };
        try {
            const response = await axios.post('http://localhost:8080/users/createUser', loginData);
            setIsSignUpModalOpen(false);
            setShowSuccess(true);
            setTimeout(() => {
                setShowSuccess(false);
            }, 3500);
            setSignUpUsername('');
                setSignUpPassword('');
        }catch(err){
            setError(err.response?.data?.message || "Failed to Sign you Up!");
        }
        console.log('New user created:', { signUpUsername});

    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setLoading(true);
        setError('');
        const loginData = {
            userName: username,
            password: password
        };
        try {
            const response = await axios.post('http://localhost:8080/users/login', loginData, {
                headers: {
                    'Content-Type': 'application/json',
                    'Accept': 'application/json'
                }
            });
            setLoading(false)
            console.log(response.status)
            if (response.status === 200) {
                const userId = response.data.userID;
                navigate(`/loggedInUser/groupPage`, {
                    state: {
                        userName: username,
                        userId: userId
                    }
                });
            } else {
                throw new Error('Unexpected error');
            }
        } catch (err) {
            setLoading(false);
            if (err.response && err.response.status===401){
                    setError('Invalid credentials, please try again.');

            }
            else{setError('Something went wrong. Please try again.');}
        }
    };

    /**
     * Logic for rendering different components based on states of different attributes
     */
    return (
        <div className="login-page">
            <div className="title">
                <i>SplitWise Spring</i>
            </div>
            <div className="login-container">
                <div className="login-form">
                    <form onSubmit={handleSubmit} className="login-form">
                        <div className="form-group">
                            <label>Username:</label>
                            <input
                                type="text"
                                value={username}
                                onChange={(e) => setUsername(e.target.value)}
                            />
                        </div>
                        <div className="form-group">
                            <label>Password:</label>
                            <input
                                type="password"
                                value={password}
                                onChange={(e) => setPassword(e.target.value)}
                            />
                        </div>
                        {error && <p className="error">{error}</p>}
                        <button type="submit" className="login-btn" disabled={loading}>
                            {loading ? 'Logging in...' : 'Login'}
                        </button>
                        <button className="login-btn" onClick={() => setIsSignUpModalOpen(true)}>
                            Sign-Up Now!
                        </button>
                    </form>
                </div>
            </div>
            {isSignUpModalOpen && (
                <div className="modal">
                    <div className="modal-content">
                        <h2 style={{color: "black"}}>Sign Up!</h2>
                        <form onSubmit={handleSignUp}>
                            <label>
                                UserName:
                                <input
                                    type="text"
                                    value={signUpUsername}
                                    onChange={(e) => setSignUpUsername(e.target.value)}
                                    required
                                />
                            </label>
                            <br/>
                            <label>
                                Password:
                                <input
                                    type="text"
                                    value={signUpPassword}
                                    onChange={(e) => setSignUpPassword(e.target.value)}
                                    required
                                />
                            </label>
                            <br/>
                            <button type="submit">Sign Up!</button>
                            <button type="button" onClick={() => setIsSignUpModalOpen(false)}>
                                Cancel
                            </button>
                        </form>
                    </div>
                </div>
            )}
            {showSuccess && (
                <div className="success-message">
                    <p>Sign Up Successful!</p>
                </div>
            )}
        </div>
    );
};

export default LoginPage;