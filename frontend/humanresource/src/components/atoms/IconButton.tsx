import React from 'react';

import type {LucideIcon} from 'lucide-react';


interface  IconButtonProps{
    icon: LucideIcon;
    onClick: ()=>void;
    title?: string;
    className?: string;

}

const IconButton: React.FC<IconButtonProps> =({icon: Icon, onClick, title, className}) =>{
    return(
        <button
            onClick={onClick}
            title={title}
            className={`flex items-center justify-center w-9 h-9 rounded-full hover:bg-blue-100 transition text-blue-600 border border-blue-200 shadow-sm ${className}`}>
        <Icon size={18}/>
        </button>
    )
}

export default IconButton