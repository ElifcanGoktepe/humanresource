import React, {type ChangeEvent } from "react";
import { InputGroup, Form } from "react-bootstrap";
import { Search } from "lucide-react";

interface SearchBarProps {
    query: string;
    onChange: (q: string) => void;
    placeholder?: string;
}

const SearchBarComponents: React.FC<SearchBarProps> = ({
                                                           query,
                                                           onChange,
                                                           placeholder = "Search...",
                                                       }) => {
    return (
        <InputGroup>
            <InputGroup.Text
                className="bg-light border-0"
                style={{ color: "var(--teal)" }}
            >
                <Search size={18} />
            </InputGroup.Text>
            <Form.Control
                type="text"
                placeholder={placeholder}
                value={query}
                onChange={(e: ChangeEvent<HTMLInputElement>) => onChange(e.target.value)}
                className="search-input"
            />
        </InputGroup>
    );
};

export default SearchBarComponents;